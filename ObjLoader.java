import java.util.StringTokenizer;
import java.io.*;

/**
 * This class provides a loader for loading .obj files into the PMesh data structure.
 *
 * @version 5-Feb-2005
 */
public class ObjLoader extends PMesh implements Serializable
{
	public ObjLoader(Scene aScene)
	{
		super(aScene);
	} // end constructor

	public boolean load(String filepath) throws FileNotFoundException
	{
		fileType = "OBJ"; // Set the file type to OBJ in case we ever want to later identify its source
		fileName = filepath; // The fileName is the ENTIRE path to the file (including the filename)
		objName = Utils.fileFromPath(filepath); // The objName is strictly the filename to begin with

		readVerts(filepath); // Read all of the verticies from the file
		readTexVerts(filepath);	// Read all of the texture verticies (if any) from the file)

		//Read any materials from the file
		//If no materials are loaded, a default one will be assigned
		if(readMaterials(filepath) == -1)
		{
			System.out.println("Not all materials could be loaded for: " + filepath);
			System.out.println("Surfaces with no materials will be assigned a default material");
		} // end if

		readSurfaces(filepath);	//Read all surface information
		countPolyVerts();		//Calculates how many vertices are in each polygon
		calcPolyNorms();		//Calculate all polygon normals (never rely on the file itself)
		calcVertNorms();		//Calculate all vertex normals based on polygon normals
		boundingSphere = calcBoundingSphere();	//Calculate the bounding sphere for raytracing

		active = true;	//We've just created an object...so make it active!
		next = null;	//Next object in the linked list is null

		//This simply loads the modelMat with an identity matrix.
		modelMat = MatrixOps.newIdentity();

		//System.out.println("\nObject loaded!\n");
		return true;

	} // end method load

	private void readVerts(String filepath)
	{
		String line;				//This will hold individual lines from the file as it is being read
		StringTokenizer tokens;		//The lines are copied into the StringTokenizer for parsing of information
		int vertNo = 0;				//Just a counter
		double xSum = 0.0, ySum = 0.0, zSum = 0.0;

		//System.out.println("About to count the vertices");

		numVerts = countVerts(filepath);	//Begin by counting all of the verticies

		// Only create an array of VertListCells if there are verticies to put in it
		if(numVerts > 0)
		{
			vertArray = new VertCell[numVerts];
			for(int i = 0; i < numVerts; i++)
				vertArray[i] = new VertCell();
		} // end if

		//Must use the try-catch because readLine will occasionally throw an IOException
		try
		{
			FileReader fr = new FileReader(filepath);
			BufferedReader objFile = new BufferedReader(fr);
			line = objFile.readLine(); // Grab the first line of the file

			// readLine will set line equal to null when it reaches the end of the file,
			// so continue reading verticies until that point
			while(line != null)
			{
				if(line.length() > 0) // ignoe blank lines
				{
					//ONLY parse lines beginning with "v " as specified by the OBJ Specification
					if(line.charAt(0) == 'v' && line.charAt(1) == ' ')
					{
						tokens = new StringTokenizer(line);
						line = tokens.nextToken(); // Clear the "v" out of the tokens
						try // parsing for a number will sometimes raise an exception
						{
							vertArray[vertNo].worldPos.x = Double.parseDouble(tokens.nextToken());
							xSum += vertArray[vertNo].worldPos.x;
							vertArray[vertNo].worldPos.y = Double.parseDouble(tokens.nextToken());
							ySum += vertArray[vertNo].worldPos.y;
							vertArray[vertNo].worldPos.z = Double.parseDouble(tokens.nextToken());
							zSum += vertArray[vertNo].worldPos.z;
							vertNo++;
						} // end try
						catch(NumberFormatException exception) {
							System.out.println("Formatting error in file: " + filepath);
						} // end catch
					} // end if
				} // end if
				line = objFile.readLine();
			}//end while
			objFile.close();
			fr.close();
		}//end try
		catch(IOException exception) {
			System.out.println("Error reading verticies from file: " + filepath);
		}//end catch
		center.x = xSum/(double)numVerts;
		center.y = ySum/(double)numVerts;
		center.z = zSum/(double)numVerts;
	} // end readVerts method

	private int countVerts(String filepath)
	{
		int vertCount = 0;	//Running total of how many verticies there are
		String line;		//This will hold individual lines from the file as it is being read

		//readLine sometimes returns an IOException error, so the try-catch is needed
		try
		{
			FileReader fr = new FileReader(filepath);			//First, setup the file for reading
			BufferedReader objFile = new BufferedReader(fr);	//The setup a BufferedReader for things like readLine

			line = objFile.readLine(); //Read the first line in the file

			//readLine sets line to null when it has reached the end of the file
			//so continue to read until then.
			while(line != null)
			{
				//No sense in checking the line if it's blank...besides, the program
				//crashes if you don't check for this first :-)
				//Also, we ONLY want to count lines that begin with "v " as specified
				//by the OBJ Specification
				if(line.length() > 0)
					if((line.charAt(0) == 'v') && (line.charAt(1) == ' '))
						vertCount++;

				//Grab the next line in the file
				line = objFile.readLine();
			}//end while

			//Done with the files for now, so we close them
			objFile.close();
			fr.close();
		}//end try
		catch(IOException exception)
		{
			System.out.println("Error counting verticies from file: " + filepath);
		}//end catch

		//System.out.println("\n" + vertCount + " verticies found");

		return vertCount;  //Return the number of verticies in the file
	}//end countVerts method

	private void readTexVerts(String filepath)
	{
		String line;				//This will hold individual lines from the file as it is being read
		StringTokenizer tokens;		//The lines are copied into the StringTokenizer for parsing of information
		int texNo = 0;  //Just a counter for placing textures in the texVertArray

		numTex = countTexVerts(filepath);

		//Only bother creating an array of VertListCells if there
		//are some verticies to put in it :-)
		if(numTex > 0)
		{
			texVertArray = new Double3D[numTex];
			//Interestingly enough, the above call will not create all of the objects, it
			//merely creates an array of references to the objects.  Here is where
			//they get created
			for(int i = 0; i < numTex; i++)
				texVertArray[i] = new Double3D();
		}//end if(numTex > 0)

		//Must use the try-catch because readLine will occasionally throw an IOException
		try
		{
			FileReader fr = new FileReader(filepath);			//First, open the file
			BufferedReader objFile = new BufferedReader(fr);	//Then, assign it to a BufferedReader
																//This allows for things like reading entire lines
			line = objFile.readLine(); //Grab the first line of the file
			//readLine will set line equal to null when it reaches the end of the file,
			//so continue reading verticies until that point
			while(line != null)
			{
				//We cannot check any characters of the line if the line is blank, so
				//we don't even bother (besides, the program crashes without this)
				if(line.length() > 0)
				{
					//ONLY parse lines beginning with "vt" as specified by the OBJ Specification
					if(line.charAt(0) == 'v' && line.charAt(1) == 't')
					{
						//A tokenizer allows for parsing through the line to pick out the numbers
						//or anything else we want for that matter :-)
						tokens = new StringTokenizer(line);
						line = tokens.nextToken();		//Clear the "vt" out of the tokens
						//unfortunately, parsing for a number will sometimes raise an exception
						//so we need this try statement
						try
						{
							texVertArray[texNo].x = Double.parseDouble(tokens.nextToken());
							texVertArray[texNo].y = Double.parseDouble(tokens.nextToken());
							if(tokens.hasMoreTokens())
								texVertArray[texNo].z = Double.parseDouble(tokens.nextToken());
							texNo++;
						}//end try
						catch(NumberFormatException exception)
						{
							System.out.println("Formatting error in file: " + filepath);
						}//end catch
					}//end if
				}//end if(line.length() > 0)
				line = objFile.readLine();
			}//end while
			//Done with the files for now, so we close them
			objFile.close();
			fr.close();
		}//end try
		catch(IOException exception)
		{
			System.out.println("Error reading verticies from file: " + filepath);
		}//end catch
	}//end readTexVerts method

	private int countTexVerts(String filepath)
	{
		int texVertCount = 0;	//The running total of how many texture verticies are in the file
		String line;			//This will hold individual lines from the file as it is being read
		//Unfortunately, the readLine() method will occasionally throw an IOException,
		//so it must be inside this try-catch block...just in case
		try
		{
			FileReader fr = new FileReader(filepath);			//First, setup the file for reading
			BufferedReader objFile = new BufferedReader(fr);	//The setup a BufferedReader for things like readLine
			line = objFile.readLine(); //Read the first line in the file
			//readLine sets line to null when it has reached the end of the file
			//so continue to read until then.
			while(line != null)
			{
				//No sense in checking the line if it's blank...besides, the program
				//crashes if you don't check for this first :-)
				//Also, we ONLY want to count lines that begin with "vt" as specified
				//by the OBJ Specification
				if(line.length() > 0)
					if((line.charAt(0) == 'v') && (line.charAt(1) == 't'))
						texVertCount++;

				//Grab the next line in the file
				line = objFile.readLine();
			}//end while
			//Done with the files for now, so we close them
			objFile.close();
			fr.close();
		}//end try
		catch(IOException exception)
		{
			System.out.println("Error reading texture coordinates from file: " + filepath);
		}//end catch
		//System.out.println("\n" + texVertCount + " texture verticies found");
		return texVertCount;
	}//end countTexVerts method

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	private void readSurfaces(String filepath)
	{
		//System.out.println("\nBuilding surfaces...");
		//This method is the last to be called out of all of the methods in this class.  Once all
		//of the vertex information has been read in, as well as the materials, they are arranged
		//into the surfaces and polygons that form that object itself...See the included file
		//CustomOBJSpecs.txt for more details

		int curMat = 0;				//this is a reference to the current material to be used
		int curIndex = 0;			//This is a temporary variable for reading in verticies of polygons
		SurfCell curSurf;			//the current surface being worked with
		PolyCell curPoly;			//the current polygon in the surface
		String matName;				//This is the material name from the obj file.  It will be used to find the material in the list
		String line;				//the current line being analyzed
		StringTokenizer token;		//For tokenizing the string to grab the different components of the line
		StringTokenizer vertTokens;	//For tokenizing the vertex entries in a polygon (surface) line
		FileReader fr;				//Just a file reading stream for the file
		BufferedReader objFile;		//this wraps around the file reader so we can do things like readLine()
		boolean inSmooth = false;	//a flag for whether or not a given face is in a surface

		try
		{
			fr = new FileReader(filepath);
			objFile = new BufferedReader(fr);
			line = objFile.readLine();
			curSurf = surfHead;
			while(line != null)
			{
				if(line.length() > 0)
				{
					switch(line.charAt(0))
					{
						case '#':
						case '!':
						case '$':
						case '\n':
						case 'v':	//These are all comments or vertex info...skip them :-)
							break;
						case 'u':
							token = new StringTokenizer(line);
							line = token.nextToken();				//"eat" up the usemtl keyword
							matName = token.nextToken();			//actually grab the material name
							for(int i = 0; i < numMats; i++)
							{
								//simply compare the stored material name to the name retrienved from the OBJ file.
								//if they match, set curMat to whatever index that material is at
								if(materials[i].materialName.toUpperCase().compareTo(matName.toUpperCase()) == 0)
								{
									curMat = i;	//Set curMat to the current material index
									break;		//Just in the interest of saving time... :-)
								}
							}
							break;
						case 's':
							token = new StringTokenizer(line);
							line = token.nextToken(); //"eat" up the s at the beginning of the line
							//If there are more tokens on the line (specifically, the word "off")
							//then we will read them in.  The only one that we really care about is if it's
							//an "off" but it must be read if it's there.
							if(token.hasMoreTokens())
								line = token.nextToken();
							//if smooth groups are turned off and no new smooth group is specified...
							if(line.toUpperCase().compareTo("OFF") == 0)
							{
								inSmooth = false;
							}
							else	//We are simply starting a new smooth group
							{
								inSmooth = true;

								if(surfHead == null)	//Create first surface
								{
									surfHead = new SurfCell();
									curSurf = surfHead;
								}
								else	//Advance to next surface
								{
									curSurf.next = new SurfCell();
									curSurf = curSurf.next;
								}

								//Assign beginning variables
								curSurf.material = curMat;
								numSurf++;			//PMesh level count of surfaces
							}//end else
							break;
						case 'f':
							if(inSmooth)	//This polygon is part of a smooth group specified by an "s" line
							{
								//At this point, surfHead will have already been initialized since we wouldn't
								//be in a smooth group unless there was an "s" line, and an "s" line would create
								//the first surface.
//								if(curSurf != null)
//								{
								addPolyToSurf(curSurf, line);
//								}
							}
							else	//This polygon is NOT part of a smooth group and is flat by itself
							{
								//One possibility, is that this is the first polygon ever specified in the file,
								//and no smooth groups have yet been set up.  In that case, surfHead would be null
								//and must first be created.
								if(surfHead == null)	//Create the first surface
								{
									surfHead = new SurfCell();
									curSurf = surfHead;
								}
								else	//Advance to next surface
								{
									curSurf.next = new SurfCell();
									curSurf = curSurf.next;
								}

								//We are now dealing with an entirely new surface

								curSurf.material = curMat;
								numSurf++;			//PMesh level count of surfaces

								addPolyToSurf(curSurf, line);
							}
							curSurf.numPoly++;	//Surface level count of the polygons
							numPolys++;			//PMesh level count of the polygons
							break;
						case 'g': //Currently, we will not deal with groups, but this is where we would.
							break;
						default:
							break;
					}
				}//end if(line.length() > 0)
				line = objFile.readLine();	//grab the next line for reading
			}//end while(line != null)
			objFile.close();
			fr.close();
		}//end try
		catch(IOException exception)
		{
			System.out.println("Error while reading surface data from: " + filepath);
		}//end catch
		System.out.println("\n" + numSurf + " surfaces found");
	} // end method readSurfaces

	private void addPolyToSurf(SurfCell curSurf, String line)
	{
		int curIndex = 0;
		StringTokenizer tokens;
		StringTokenizer vertTokens;
		PolyCell curPoly;
		PolyListCell curVertPoly;
		VertListCell curVert;

		tokens = new StringTokenizer(line);
		line = tokens.nextToken(); //"eat" up the f at the beginning of the line

		if(curSurf.polyHead == null) //This is the first polygon in the surface
		{
			curSurf.polyHead = new PolyCell();
			curPoly = curSurf.polyHead;
		}
		else //Other polygons are already in the surface
		{
			curPoly = curSurf.polyHead;		//Begin at the first polygon
			while(curPoly.next != null)		//Move to the next polygon as long as it exists
				curPoly = curPoly.next;

			curPoly.next = new PolyCell();	//Create a new polygon at the end of the list
			curPoly = curPoly.next;
		}

		//At this point, we are dealing with an entirely new polygon!
		curPoly.parentSurf = curSurf;
		while(tokens.hasMoreTokens())	//Loop for constructing an entire polygon...one vertex at a time!
		{
			line = tokens.nextToken();
			vertTokens = new StringTokenizer(line, "/");			//A tokenizer separated by '/'
			curIndex = Integer.parseInt(vertTokens.nextToken());	//Grab the first number (a vertex index)
			if(curPoly.vert == null)	//This is our first vertex in the polygon
			{
				curPoly.vert = new VertListCell();
				curVert = curPoly.vert;
			}
			else	//Other verticies have already been added
			{
				curVert = curPoly.vert;
				while(curVert.next != null)
					curVert = curVert.next;

				curVert.next = new VertListCell();
				curVert = curVert.next;
			}
			curVert.vert = curIndex-1;	//Assign the vertex index
			if(vertArray[curIndex-1].polys == null)
			{
				vertArray[curIndex-1].polys = new PolyListCell();
				curVertPoly = vertArray[curIndex-1].polys;
			}
			else
			{
				curVertPoly = vertArray[curIndex-1].polys;
				while(curVertPoly.next != null)
					curVertPoly = curVertPoly.next;
				curVertPoly.next = new PolyListCell();
				curVertPoly = curVertPoly.next;
			}
			curVertPoly.poly = curPoly;
			curIndex = line.indexOf('/');	//Find the first '/' if it exists
			if(curIndex > -1)
			{
				if(line.charAt(curIndex + 1) != '/')
				{
					curIndex = Integer.parseInt(vertTokens.nextToken());	//Grab the texture vertex index
					curVert.tex = curIndex-1;
				}//end check for second /
			}//end check for ANY /'s
		}//end while
	} // end method addPolyToSurf

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	private int readMaterials(String filepath)
	{
		int matNo = 0;
		//The current material number - index into the materials array
		String line;
		//This will hold individual lines from the file as it is being read
		String name;
		StringBuffer fileNameList;
		//This (temporarily) holds the file names listed in the obj file
		StringTokenizer fileNames;
		//This is a tokenized list of files for counting the materials
		StringTokenizer tokens;
		//This is merely for parsing through the read lines to grab the filename
		FileReader fr;
		//This will be for opening the files
		BufferedReader objFile;
		//This will hold the open file so we can read entire lines from it at once
		BufferedReader mtlFile;
		//This will hold the open file so we can read entire lines from it at once

		//First, we'll try to retrieve a list of the material libraries
		// from the obj file
		//this is in a try-catch block since the readLine method can
		// sometimes throw an IOException
		try
		{
			fr = new FileReader(filepath);
			objFile = new BufferedReader(fr);
			fileNameList = new StringBuffer();
			line = objFile.readLine();		//Read the first line in the file
			//readLine sets line to null when it has reached the end of the file
			//so continue to read until then.
			while(line != null)
			{
				//No sense in checking the line if it's blank...besides, the program
				//crashes if you don't check for this first :-)
				if(line.length() > 6)
				{
					if(line.substring(0, 6).toLowerCase().compareTo("mtllib") == 0)
					{
						tokens = new StringTokenizer(line);
						line = tokens.nextToken();
						//Get rid of the mtllib token first
						fileNameList.append(" " + tokens.nextToken());
						//append the filenames one by one to a list
					}
				}
				//Grab the next line in the file
				line = objFile.readLine();
			}//end while

			//Done with the files for now, so we close them
			objFile.close();
			fr.close();
			fileNames = new StringTokenizer(fileNameList.toString());
//DEBUG
			StringTokenizer debug = new StringTokenizer(fileNameList.toString());
			System.out.println("\nThe following material libraries were found:");
			while(debug.hasMoreTokens())
			{
				System.out.println("   * " + debug.nextToken());
			}
			System.out.println();
//END DEBUG
		}//end try
		catch(IOException exception)
		{
			System.out.println("Error reading material libraries from: " + filepath);
			return -1;
		}//end catch

		//This returns how many materials are in the file(s) and adds it to the one default that
		//always exists
		numMats += countMaterials(fileNameList.toString(), filepath);
		materials = new MaterialCell[numMats];
		for(int i = 0; i < numMats; i++)
			materials[i] = new MaterialCell();
		//Now that the material library filenames have been retrieved
		// (and stored in the StringTokenizer called fileNames) we can
		// procede to open them and add the materials
			while(fileNames.hasMoreTokens())
			{
				try
				{
					fr = new FileReader(filepath.substring(0, (filepath.length()-objName.length())) + fileNames.nextToken());
					mtlFile = new BufferedReader(fr);
				}
				catch(FileNotFoundException exception)
				{
					System.out.println("Error opening material library for reading");
					return -1;
				}
				try
				{
					line = mtlFile.readLine();
				}
				catch(IOException exception)
				{
					System.out.println("Error reading from material library");
					return -1;
				}
				while(line != null)
				{
					if(line.length() > 0)
					{
						//Things to recall at this point in the code:
						//   * materials[] is the array of MaterialCells in the PMesh that
						//     this class extends
						//   * matNo is the current material being read in.  This gets
						//     incremented every time a newmtl line is found.  It is also
						//     the index into the materials[] array
						//   * materials[0] is already set to a default material (thanks
						//     to the constructor for MaterialCell).  It will remain THE
						//     default material, and all subsequent materials will be added
						//     beginning at index 1
						switch(line.charAt(0))
						{
							case '#':	//Comment
							case '!':
							case '$':
							case '\n':
								break;
							case 'n':	//(newmtl) new material
								tokens = new StringTokenizer(line);	//Holds the line for parsing to get information,

								matNo++; //Working on a new material
								name = tokens.nextToken();
								name = tokens.nextToken(); //Eat up the "newmtl" keyword
								materials[matNo].materialName = new String(name); //Assign the material name
								break;
							case 'K':	//(Ka, Kd, Ks) color coefficients
								tokens = new StringTokenizer(line);
								try
								{
									switch(line.charAt(1))
									{
										case 'a':	//(Ka) ambient color coefficients
											line = tokens.nextToken();
											materials[matNo].ka[0] = Float.parseFloat(tokens.nextToken());
											materials[matNo].ka[1] = Float.parseFloat(tokens.nextToken());
											materials[matNo].ka[2] = Float.parseFloat(tokens.nextToken());
											if(tokens.hasMoreTokens())	//Test for the existance of a possible alpha value
												materials[matNo].ka[3] = Float.parseFloat(tokens.nextToken());
											break;
										case 'd':	//(Kd) diffuse color coefficients
											line = tokens.nextToken();
											materials[matNo].kd[0] = Float.parseFloat(tokens.nextToken());
											materials[matNo].kd[1] = Float.parseFloat(tokens.nextToken());
											materials[matNo].kd[2] = Float.parseFloat(tokens.nextToken());
											if(tokens.hasMoreTokens())	//Test for the existance of a possible alpha value
												materials[matNo].kd[3] = Float.parseFloat(tokens.nextToken());
											break;
										case 's':	//(Ks) specular color coefficients
											line = tokens.nextToken();
											materials[matNo].ks[0] = Float.parseFloat(tokens.nextToken());
											materials[matNo].ks[1] = Float.parseFloat(tokens.nextToken());
											materials[matNo].ks[2] = Float.parseFloat(tokens.nextToken());
											if(tokens.hasMoreTokens())	//Test for the existance of a possible alpha value
												materials[matNo].ks[3] = Float.parseFloat(tokens.nextToken());
											break;
										default:
											break;
									}
								}//end try for reading color coefficients
								catch(NumberFormatException exception)
								{
									System.out.println("Error while reading color coefficients from material file");
									return -1;
								}//end catch for reading color coefficients
								break;
							case 'm':	//(map_Ka, map_Kd, map_Ks, map_d) texture maps
								tokens = new StringTokenizer(line);
								switch(line.charAt(4))
								{
									case 'K':	//(map_Ka, map_Kd, map_Ks) texture maps
										switch(line.charAt(5))
										{
											case 'a':	//(map_Ka) ambient texture map
												line = tokens.nextToken();
												materials[matNo].mapKa = tokens.nextToken();
												break;
											case 'd':	//(map_Kd) diffuse texture map
												line = tokens.nextToken();
												materials[matNo].mapKd = tokens.nextToken();
												break;
											case 's':	//(map_Ks) specular texture map
												line = tokens.nextToken();
												materials[matNo].mapKs = tokens.nextToken();
												break;
											default:
												break;
										}
										break;
									case 'd':	//(map_d) transparency map
										line = tokens.nextToken();
												materials[matNo].mapD = tokens.nextToken();
										break;
									default:
										break;
								}
								break;
							case 'e':	//(emm) emmisive color coefficients
								tokens = new StringTokenizer(line);
								try
								{
									line = tokens.nextToken();
									materials[matNo].emmColor[0] = Float.parseFloat(tokens.nextToken());
									materials[matNo].emmColor[1] = Float.parseFloat(tokens.nextToken());
									materials[matNo].emmColor[2] = Float.parseFloat(tokens.nextToken());
								}//end try for reading emmisive color
								catch(NumberFormatException exception)
								{
									System.out.println("Error while reading emmisive color from material file");
									return -1;
								}//end catch for reading emmisive color
								break;
							case 'T':	//(Tr) transparency
								tokens = new StringTokenizer(line);
								try
								{
									line = tokens.nextToken();
									materials[matNo].tr = Float.parseFloat(tokens.nextToken());
								}//end try for reading transparency
								catch(NumberFormatException exception)
								{
									System.out.println("Error while reading transparency from material file");
									return -1;
								}//end catch for reading transparency
								break;
							case 'N':	//(Ns, Ni) shininess or refraction index
								tokens = new StringTokenizer(line);
								try
								{
									switch(line.charAt(1))
									{
										case 's':	//(Ns) shininess
											line = tokens.nextToken();
											materials[matNo].shiny = Float.parseFloat(tokens.nextToken());
											materials[matNo].shiny /= 1000.0;	//Wavefront shininess is from [0, 1000]
											materials[matNo].shiny *= 128.0;	//So rescale for OpenGL... [0, 128]
											break;
										case 'i':	//(Ni) refraction index
											line = tokens.nextToken();
											materials[matNo].refIndex = Float.parseFloat(tokens.nextToken());
											break;
										default:
											break;
									}
								}//end try for reading shininess or refraction index
								catch(NumberFormatException exception)
								{
									System.out.println("Error while reading shininess or refraction index from material file");
									return -1;
								}//end catch for reading shininess or refraction index
								break;
							/*case 'r':	//(refl_c, refr_c) reflection/refraction coefficients
								tokens = new StringTokenizer(line);
								try
								{
									switch(line.charAt(3))
									{
										case 'l':
											line = tokens.nextToken();
											materials[matNo].reflectCoeff[0] = Float.parseFloat(tokens.nextToken());
											materials[matNo].reflectCoeff[1] = Float.parseFloat(tokens.nextToken());
											materials[matNo].reflectCoeff[2] = Float.parseFloat(tokens.nextToken());
											if(tokens.hasMoreTokens())
												materials[matNo].reflectCoeff[3] = Float.parseFloat(tokens.nextToken());
											break;
										case 'r':
											line = tokens.nextToken();
											materials[matNo].refractCoeff[0] = Float.parseFloat(tokens.nextToken());
											materials[matNo].refractCoeff[1] = Float.parseFloat(tokens.nextToken());
											materials[matNo].refractCoeff[2] = Float.parseFloat(tokens.nextToken());
											if(tokens.hasMoreTokens())
												materials[matNo].refractCoeff[3] = Float.parseFloat(tokens.nextToken());
											break;
										default:
											break;
									}
								}//end try for reading reflection/refraction coefficients
								catch(NumberFormatException exception)
								{
									System.out.println("Error while reading reflection/refraction coefficients from material file");
									return -1;
								}//end catch for reading reflection/refraction coefficients
								break;*/
							case 'L':	//(Lc) line color
								tokens = new StringTokenizer(line);
								try
								{
									line = tokens.nextToken();
									materials[matNo].lineColor[0] = Float.parseFloat(tokens.nextToken());
									materials[matNo].lineColor[1] = Float.parseFloat(tokens.nextToken());
									materials[matNo].lineColor[2] = Float.parseFloat(tokens.nextToken());
									if(tokens.hasMoreTokens())
										materials[matNo].lineColor[3] = Float.parseFloat(tokens.nextToken());
								}//end try for reading line color
								catch(NumberFormatException exception)
								{
									System.out.println("Error while reading line color from material file");
									return -1;
								}//end catch for reading line color
								break;
							case 'd':	//(ds) double sided
								if(line.charAt(1) == 's')
								{
									tokens = new StringTokenizer(line);
									try
									{
										line = tokens.nextToken();
										if(Integer.parseInt(tokens.nextToken()) == 1)
											materials[matNo].doubleSided = true;
										//else it defaults to "false"
									}//end try for reading double sided flag
									catch(NumberFormatException exception)
									{
										System.out.println("Error while reading double sided flag from material file");
										return -1;
									}//end catch for reading double sided flag
								}
								break;
							default:
								break;
						}//end switch
					}//end if(line.length() > 0)

					try
					{
						line = mtlFile.readLine();
					}
					catch(IOException exception)
					{
						System.out.println("Error reading from material library");
						return -1;
					}
				}//end while(line != null)
				try
				{
					mtlFile.close();
					fr.close();
				}
				catch(IOException exception)
				{
					System.out.println("Error closing file...somehow");
					return -1;
				}
			}
		return 0;
	}//end readMaterials

	public int countMaterials(String fileNameList, String filepath)
	{
		int matCount = 0;
		String line;
		FileReader fr;
		BufferedReader mtlFile;
		StringTokenizer fileNames = new StringTokenizer(fileNameList.toString());
		try
		{
			while(fileNames.hasMoreTokens())
			{
				fr = new FileReader(filepath.substring(0, (filepath.length()-objName.length())) + fileNames.nextToken());
				mtlFile = new BufferedReader(fr);
				line = mtlFile.readLine();
				while(line != null)
				{
					if(line.length() > 6)
					{
						if(line.substring(0, 6).toLowerCase().compareTo("newmtl") == 0)
							matCount++;
					}
					line = mtlFile.readLine();
				}
				mtlFile.close();
				fr.close();
			}
		}
		catch(FileNotFoundException exception)
		{
			System.out.println("Error opening material library for counting");
			return 0;
		}
		catch(IOException exception)
		{
			System.out.println("Error counting materials from material library");
			return 0;
		}
//DEBUG
		System.out.println(matCount + " materials found");
//END DEBUG
		return matCount;
	}//end countMaterials

	public void countPolyVerts()
	{
		SurfCell curSurf;
		PolyCell curPoly;
		VertListCell curVert;
		curSurf = surfHead;
		while(curSurf != null)
		{
			curPoly = curSurf.polyHead;
			while(curPoly != null)
			{
				curVert = curPoly.vert;
				while(curVert != null)
				{
					curPoly.numVerts++;
					curVert = curVert.next;
				}
				curPoly = curPoly.next;
			}
			curSurf = curSurf.next;
		}
	}//end countPolyVerts method

	/**
	 * @param String - the name of the file
	 * @param String - the name of the file (without the path but with the extension)
	 * 					containing the materials used by the mesh
	 */
	public boolean save(String filename, String materialFilename)
	{
		PMesh.SurfCell surf = this.surfHead;
		PMesh.PolyCell poly;
		PMesh.VertListCell vList;

		try
		{
			int i, j, k;
			BufferedWriter fout = new BufferedWriter(new FileWriter(filename));

			fout.write("#PMesh data structure needs modification to remember group name\n");
			fout.write("mtllib "+materialFilename+"\n");
			for(i=0 ; i<vertArray.length ; i++)
			{
				fout.write("v "+vertArray[i].worldPos.x+
							" "+vertArray[i].worldPos.y+
							" "+vertArray[i].worldPos.z+"\n");
			}
			fout.write("# "+i+" vertices"+"\n");
			fout.write("\n");
			i=0;
			while(surf!=null)
			{
				poly=surf.polyHead;
				fout.write("g surface"+i+"\n");
				fout.write("usemtl "+materials[surf.material].materialName+"\n");
				while(poly!=null)
				{
					vList=poly.vert;
					fout.write("f ");
					while(vList!=null)
					{
						fout.write((vList.vert+1)+" ");
						vList=vList.next;
					}
					fout.write("\n");
					poly=poly.next;
				}
				surf=surf.next;
				i++;
			}
			fout.close();
			return true;
		}//end try block
		catch (java.io.IOException ev)
		{
			System.out.println("Could not open file for output, operation terminated");
			return false;
		} // end catch
	} // end method save

} // end class ObjLoader
