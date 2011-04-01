
import java.util.StringTokenizer;
import java.io.*;
import java.nio.*;
import java.util.ArrayList;
import static javax.media.opengl.GL2.*;
import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import com.jogamp.common.nio.*;
/**
 * This class provides a loader for loading .obj files into the PMesh data structure.
 *
 * @version 3-Jan-2011 corrections to the objLoader
 * @version 29-Jan-2011 adds VertexArrays and Vertex Buffer Objects
 */
public class ObjLoaderBuffer extends PMesh implements Serializable
{
	public String mtlFileName =null;
	public ObjLoaderBuffer(Scene aScene)
	{
		super(aScene);
	} // end constructor
	
	public void loadBuffers(){
		SurfCell curSurf;			//the current surface being worked with
		PolyCell curPoly;			//the current polygon in the surface
		VertListCell curVertLC;

		curSurf = surfHead;
		int vertCount;
		while(curSurf != null){
			int vertsPerPrim;
			vertCount = 0;
			curPoly= curSurf.polyHead;
			//vertCount += curPoly.numVerts;
			vertsPerPrim = curPoly.numVerts;
			while (curPoly != null){
				vertCount += curPoly.numVerts;
				if(curPoly.numVerts != vertsPerPrim){
					System.out.printf("Surface %s: Unequal number of vertices ",
							curSurf.name);
					System.out.printf("\n    First prim had %d Cur Prim has %d\n", 
							curPoly.numVerts, vertsPerPrim);
					return;					
				}
				curPoly = curPoly.next;
			}
			curSurf.numVerts = vertCount;
			float vertices [] = new float[vertCount*3];
			int vInd = 0;
			float normals [] = new float[vertCount*3];
			int nInd = 0;
			curPoly=curSurf.polyHead;
			while(curPoly != null){
				curVertLC = curPoly.vert;
				while(curVertLC != null){
				//for(int i = 0; i < curPoly.numVerts; i++);{
					VertCell curVert = vertArray.get(curVertLC.vert);
					vertices[vInd++] = (float)curVert.worldPos.x;
					vertices[vInd++] = (float)curVert.worldPos.y;
					vertices[vInd++] = (float)curVert.worldPos.z;
					normals[nInd++]= (float)vertNormArray[curVertLC.vert].x;
					normals[nInd++]= (float)vertNormArray[curVertLC.vert].y;
					normals[nInd++]= (float)vertNormArray[curVertLC.vert].z;
					curVertLC = curVertLC.next;
				}
				curPoly = curPoly.next;
			}
			// now put vertices and normals into VertexArray or Buffer
			curSurf.vertexBuffer = Buffers.newDirectFloatBuffer(vertices.length);
			curSurf.vertexBuffer.put(vertices);
			curSurf.vertexBuffer.rewind();
			
			curSurf.normalBuffer =  Buffers.newDirectFloatBuffer(normals.length);
			curSurf.normalBuffer.put(normals);
			curSurf.normalBuffer.rewind();
		
			curSurf = curSurf.next;
		}

	}
	
	public boolean load(String filepath) throws FileNotFoundException
	{
		fileType = "OBJ"; // Set the file type to OBJ in case we ever want to later identify its source
		filePath = Utils.dirFromPath(filepath); // The fileName is the ENTIRE path to the file (including the filename)
		objName = Utils.fileFromPath(filepath); // The objName is strictly the filename to begin with
		readVerts(filepath); // Read all of the vertices from the file
		//readTexVerts(filepath);	// Read all of the texture vertices (if any) from the file)

		//Read any materials from the file
		//If no materials are loaded, a default one will be assigned
		if(readMaterials(filepath) == -1)
		{
			System.out.println("Not all materials could be loaded for: " + filepath);
			System.out.println("Surfaces with no materials will be assigned a default material");
			
		} // end if

		readSurfaces(filepath);	//Read all surface information
		countPolyVerts();		//Calculates how many vertices are in each polygon
		calcPolyNorms();	//Calculate all polygon normals (never rely on the file itself)
		calcVertNorms();		//Calculate all vertex normals based on polygon normals
		boundingSphere = calcBoundingSphere();	//Calculate the bounding sphere for raytracing

		active = true;	//We've just created an object...so make it active!
		next = null;	//Next object in the linked list is null

		//This simply loads the modelMat with an identity matrix.
		modelMat = MatrixOps.newIdentity();

		//System.out.println("\nObject loaded!\n");
		loadBuffers();
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
			vertArray = new ArrayList<VertCell>();
			vertUsedArray = new ArrayList<SurfCell>();
			for(int i = 0; i < numVerts; i++){
				vertArray.add(i, new VertCell());
				vertUsedArray.add(i, null);
			}
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
							vertArray.get(vertNo).worldPos.x = Double.parseDouble(tokens.nextToken());
							xSum += vertArray.get(vertNo).worldPos.x;
							vertArray.get(vertNo).worldPos.y = Double.parseDouble(tokens.nextToken());
							ySum += vertArray.get(vertNo).worldPos.y;
							vertArray.get(vertNo).worldPos.z = Double.parseDouble(tokens.nextToken());
							zSum += vertArray.get(vertNo).worldPos.z;
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

		//System.out.println("\n" + vertCount + " vertices found");

		return vertCount;  //Return the number of vertices in the file
	}//end countVerts method
	
	/**
	 * This method saves all loaded materials to a .mtl file.
	 */	
	public void saveMaterials(String filePath){
		try{
			BufferedWriter fout = new BufferedWriter(new FileWriter(filePath/*+".mtl"*/));
			MaterialCell mat[];
			mat=this.materials;
			for(int i=1; i<mat.length ; i++)//make i intialized to 1 if you want to avoid saving default material
			{
				fout.write("newmtl "+mat[i].materialName+"\n");
				fout.write("Ka "+mat[i].ka.toString()+"\n");
				fout.write("Kd "+mat[i].kd.toString()+"\n");
				fout.write("Ks "+mat[i].ks.toString()+"\n");
				if(!(mat[i].emmColor.r == 0.0 && mat[i].emmColor.g ==0.0 && mat[i].emmColor.b == 0.0))
					fout.write("e "+mat[i].emmColor.toString()+"\n");
				// e - emitted color
				fout.write("Ns "+(mat[i].shiny)+"\n");
				// Ns - shininess (0-128) Phong cos exponent in OpenGL form
				fout.write("Ni "+mat[i].refractiveIndex+"\n");
				//Ni - index of refraction
				fout.write("Lc "+ mat[i].lineColor.toString()+"\n");
				//Lc - line color
				if(!(mat[i].transmissionFilter.r == 0.0 && mat[i].transmissionFilter.g ==0.0 && mat[i].transmissionFilter.b == 0.0))
					fout.write("Tf "+mat[i].transmissionFilter.toString()+"\n");
				// Tf - transmission filter - selects color of light transmitted
				if(!(mat[i].reflectivity.r == 0.0 && mat[i].reflectivity.g ==0.0 && mat[i].reflectivity.b == 0.0))
					fout.write("Ir "+mat[i].reflectivity.toString()+"\n");
				// Ir- intensity of reflected ray
				if(!(mat[i].refractivity.r == 0.0 && mat[i].refractivity.g ==0.0 && mat[i].refractivity.b == 0.0))
					fout.write("It "+mat[i].refractivity.toString()+"\n");
				// It - intensity of transmitted (refracted) ray

				fout.write("\n");
			}
			fout.close();
		}
		catch (IOException ex)
		{
			System.out.println("Could not open file for material output, operation terminated");
			ex.printStackTrace();
		}
	}	
/*	public String getMaterialFile(String filepath)
	{
		String line, tmpName = null;		//This will hold individual lines from the file as it is being read
		//readLine sometimes returns an IOException error, so the try-catch is needed
		try
		{
			FileReader fr = new FileReader(filepath);			//First, setup the file for reading
			BufferedReader objFile = new BufferedReader(fr);	//The setup a BufferedReader for things like readLine
			String objFileName = Utils.fileFromPath(filepath);
			/// BEFORE returning need to strip the obj file name off file path and replace with mtlFileName
			String filePath = filepath.replaceAll(objFileName, "");			
			line = objFile.readLine(); //Read the first line in the file
			//readLine sets line to null when it has reached the end of the file
			//so continue to read until then.
			while(line != null)
			{
				//No sense in checking the line if it's blank...besides, the program
				//crashes if you don't check for this first :-)
				String [] splits = line.split(" ");
				if(splits[0].equals("mtllib")){//next token is file name
					tmpName = splits[1];
				}
				if(tmpName != null)
					return filePath+tmpName;
				//Grab the next line in the file
				line = objFile.readLine();
			}//end while
			//Done with the files for now, so we close them
			objFile.close();
			fr.close();
		}//end try
		catch(IOException exception)
		{
			System.out.println("Error retrieving material file name from file: " + filepath);
		}//end catch
		return null;  //Return the material file name - null is not found
	}//end countVerts method
*/
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
			texVertArray = new ArrayList<Double3D>();
			//Interestingly enough, the above call will not create all of the objects, it
			//merely creates an array of references to the objects.  Here is where
			//they get created
			for(int i = 0; i < numTex; i++)
				texVertArray.add(i, new Double3D());
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
							texVertArray.get(texNo).x = Double.parseDouble(tokens.nextToken());
							texVertArray.get(texNo).y = Double.parseDouble(tokens.nextToken());
							if(tokens.hasMoreTokens())
								texVertArray.get(texNo).z = Double.parseDouble(tokens.nextToken());
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
							boolean found = false;
							int i =0;
							while(!found && i < numMats)
							//for(int i = 0; i < numMats; i++)
							{
								//simply compare the stored material name to the name retrienved from the OBJ file.
								//if they match, set curMat to whatever index that material is at
								if(materials[i].materialName.toUpperCase().compareTo(matName.toUpperCase()) == 0)
								{
									curMat = i;	//Set curMat to the current material index
									found = true;
								}
								i++;
							}
							if (!found)
							{
								System.out.printf("Group %s material %s not found - using default\n", 
										curSurf.name, matName);
								curSurf.material = 0;
							}
							else
								curSurf.material = curMat;
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
								//curSurf.smooth = inSmooth;
							}//end else
							break;
						case 'f':
							if(curSurf != null)
							{
								addPolyToSurf(curSurf, line, inSmooth);
							}
							
							else	//no active surface - create a "default" surface and add this poly to it
							{
								System.out.printf("ReadSurfaces: No active surface available\n");
								System.out.printf("Creating a default surface\n");
								if(inSmooth)
									surfHead = new SurfCell("default");
								else
									surfHead = new SurfCell("default");
								curSurf = surfHead;
								addPolyToSurf(curSurf, line, inSmooth);								
							}
							curSurf.numPoly++;	//Surface level count of the polygons
							numPolys++;			//PMesh level count of the polygons
							break;
							
							
						case 'g': //Starts a new surface - if inSmooth is true set the smooth flag in
							          // that surface
							token = new StringTokenizer(line);
							line = token.nextToken(); //"eat" up the g at the beginning of the line
							//If there are more tokens on the line (specifically a group name)
							//then we will read it in.  
							if(token.hasMoreTokens())
								line = token.nextToken();
							if(line == null)
								line = new String("Group"+numSurf);
							if(surfHead == null)	//Create first surface
							{
								surfHead = new SurfCell(line);
								curSurf = surfHead;
							}
							else	//Advance to next surface
							{
								curSurf.next = new SurfCell(line);
								curSurf = curSurf.next;
							}
							//Assign beginning variables
							numSurf++;			//PMesh level count of surfaces							break;
						default:
							break;
					} //end switch
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
		System.out.printf("\n %d vertices  %d surfaces\n", numVerts, numSurf);
		//System.out.println("\n" + numSurf + " surfaces found");
	} // end method readSurfaces

	private void addPolyToSurf(SurfCell curSurf, String line, boolean inSmooth)
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
		curPoly.numVerts=0;

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
 //NOTE: copying of vertices should not be necessary since each surface is drawn by itself and
// * each surface will either be drawn smoothed or flat and either way the correct normal will be
// * set and used for each polygon - if a surface is flat no vertex normals will be calculated - could
// * still be a problem if indeed vertices are reused in smoothed surfaces but that is apparently not true??	
////////All wrong - vertex normals are not recalculated at draw time so any vertex can NOT be in 
//////////multiple surfaces
 
			////// CHECK for vertex copy here
			// if inSmooth then go ahead and setup the vertex references without regard to
			//   what is in vertexUsedArray
			// if !inSmooth then this is a one poly surface and if the vertex has already been
			//    used (as indicated in vertexUsedArray then a new copy of the vertex must be made
			
			if(!inSmooth){  //we are in an unsmoothed surface [often a one poly surface] -check to see if vertex(curIndex-1) has 
								// already been used in another surface
				if(curIndex <= vertUsedArray.size() && vertUsedArray.get(curIndex - 1)!= null){
					// make a copy of the vertex that curIndex-1 indicates
					vertArray.add(numVerts, new VertCell(vertArray.get(curIndex-1)));
					//vertNormArray.add(numNorms++, new Double3D(vertNormArray.get(curIndex-1)));
					vertUsedArray.add(numVerts++, curSurf);
					curIndex = numVerts;
				}
			}
			else if(vertUsedArray.get(curIndex-1)!= null && vertUsedArray.get(curIndex-1) != curSurf){  
				// we are in a smoothing group
				//but this vertex has already been used in another surface
				// so find out if there is already a copy in this surface
				int copyIndex = findCopyInSurf(curSurf, vertArray.get(curIndex-1).worldPos);
				if( copyIndex == -1){
					// make a copy of the vertex that curIndex-1 indicates
					vertArray.add(numVerts, new VertCell(vertArray.get(curIndex-1)));
					//vertNormArray.add(numNorms++, new Double3D(vertNormArray.get(curIndex-1)));
					vertUsedArray.add(numVerts++, curSurf);
					curIndex = numVerts;	
				}
				else {
					curIndex = copyIndex+1;
				}
			}
		
			curVert.vert = curIndex-1;	//Assign the vertex index
			//curPoly.numVerts++;
			vertUsedArray.set(curIndex-1, curSurf);
			
			/*
			 * If we are not in a smoothing group at the moment do not build the
			 * linked list specifying polygon membership for the vertex - when normals
			 * are calculated any vertex with no list will be assumed to be part of  a
			 * polygon which is to be flat shaded and the polygon normal will be used
			 * for the vertex normals of each vertex.
			 */
			if(inSmooth){
				if(vertArray.get(curIndex-1).polys == null)
				{
					vertArray.get(curIndex-1).polys = new PolyListCell();
					curVertPoly = vertArray.get(curIndex-1).polys;
				}
				else
				{
					curVertPoly = vertArray.get(curIndex-1).polys;
					while(curVertPoly.next != null)
						curVertPoly = curVertPoly.next;
					curVertPoly.next = new PolyListCell();
					curVertPoly = curVertPoly.next;
				}
				curVertPoly.poly = curPoly;
			}
			else vertArray.get(curIndex-1).polys = null;
			
			curIndex = line.indexOf('/');	//Find the first '/' if it exists
			if(curIndex > -1)
			{
				if(line.charAt(curIndex + 1) != '/')
				{
					curIndex = Integer.parseInt(vertTokens.nextToken());	//Grab the texture vertex index
					curVert.tex = curIndex-1;
				}//end check for second /
//////////////////////////////////////////////////////////////////////////////////////////////
			
			}//end check for ANY /'s
		}//end while
	} // end method addPolyToSurf

	public int findCopyInSurf(SurfCell curSurf, Double3D findme){
		int i = 0;
		while ( i < numVerts && (vertUsedArray.get(i) != curSurf || vertUsedArray.get(i)== null || !(vertArray.get(i).worldPos.equals(findme)))){
			i++;			
		}
		if(i == numVerts) return -1;
		else return i;
		
	}
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
				line = line.trim();
				//No sense in checking the line if it's blank...besides, the program
				//crashes if you don't check for this first :-)
				if(line.length() > 6)
				{
					String [] splits = line.split(" ");
					if(splits[0].equals("mtllib")){//next token is file name
						mtlFileName = splits[1];
						fileNameList.append(splits[1]);
						for(int i = 2; i < splits.length; i++){
							fileNameList.append(" "+splits[i]);
						}
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
				line = line.trim();
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
								materials[matNo].ka.r = Double.parseDouble(tokens.nextToken());
								materials[matNo].ka.g = Double.parseDouble(tokens.nextToken());
								materials[matNo].ka.b = Double.parseDouble(tokens.nextToken());
								if(tokens.hasMoreTokens())	//Test for the existance of a possible alpha value
									materials[matNo].ka.a = Double.parseDouble(tokens.nextToken());
								break;
							case 'd':	//(Kd) diffuse color coefficients
								line = tokens.nextToken();
								materials[matNo].kd.r = Double.parseDouble(tokens.nextToken());
								materials[matNo].kd.g = Double.parseDouble(tokens.nextToken());
								materials[matNo].kd.b = Double.parseDouble(tokens.nextToken());
								if(tokens.hasMoreTokens())	//Test for the existance of a possible alpha value
									materials[matNo].kd.a = Double.parseDouble(tokens.nextToken());
								break;
							case 's':	//(Ks) specular color coefficients
								line = tokens.nextToken();
								materials[matNo].ks.r = Double.parseDouble(tokens.nextToken());
								materials[matNo].ks.g = Double.parseDouble(tokens.nextToken());
								materials[matNo].ks.b = Double.parseDouble(tokens.nextToken());
								if(tokens.hasMoreTokens())	//Test for the existance of a possible alpha value
									materials[matNo].ks.a = Double.parseDouble(tokens.nextToken());
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
					case 'I':
						tokens = new StringTokenizer(line);
						switch(line.charAt(1)){
							case 'r': // reflectivity - one or three values should be specified
								int count = 0;
								line = tokens.nextToken();
								do{
									materials[matNo].reflectivity.r = Double.parseDouble(tokens.nextToken());
									count++;
								}
								while (tokens.hasMoreTokens() && count < 3);
								if (count == 1){
									//materials[matNo].reflOneValue = true;
									materials[matNo].reflectivity.g = materials[matNo].reflectivity.r;
									materials[matNo].reflectivity.b = materials[matNo].reflectivity.r;
								}
								//else if(count ==3) materials[matNo].reflOneValue = false;
								else System.out.printf("Error reading reflectivity: count=%d\n", count);
								break; // end case 'r'
								
							case 't':
								int cnt = 0;
								line = tokens.nextToken();
								do{
									materials[matNo].refractivity.r = Double.parseDouble(tokens.nextToken());
									cnt++;
								}
								while (tokens.hasMoreTokens() && cnt < 3) ;
								
								if (cnt == 1){
									//materials[matNo].refrOneValue = true;
									materials[matNo].refractivity.g = materials[matNo].refractivity.r;
									materials[matNo].refractivity.b = materials[matNo].refractivity.r;
								}
								//else if(cnt ==3) materials[matNo].refrOneValue = false;
								else System.out.printf("Error reading refractivity: count=%d\n", cnt);
								break; // end case 't'
							}
							break; // end case 'I'

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
						case 'e':	//(emm) emissive color coefficients
							tokens = new StringTokenizer(line);
							try
							{
								line = tokens.nextToken();
								materials[matNo].emmColor.r = Double.parseDouble(tokens.nextToken());
								materials[matNo].emmColor.g = Double.parseDouble(tokens.nextToken());
								materials[matNo].emmColor.b = Double.parseDouble(tokens.nextToken());
							}//end try for reading emissive color
							catch(NumberFormatException exception)
							{
								System.out.println("Error while reading emissive color from material file");
								return -1;
							}//end catch for reading emissive color
							break;
						case 'T':	//(Tf) transmission filter
							tokens = new StringTokenizer(line);
							try
							{
								line = tokens.nextToken();
								materials[matNo].transmissionFilter.r = Double.parseDouble(tokens.nextToken());
								materials[matNo].transmissionFilter.g = Double.parseDouble(tokens.nextToken());
								materials[matNo].transmissionFilter.b = Double.parseDouble(tokens.nextToken());
							}//end try for reading transparency
							catch(NumberFormatException exception)
							{
								System.out.println("Error while reading transmissionFilter from material file");
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
									materials[matNo].shiny = Double.parseDouble(tokens.nextToken());
									// ASSUME shininess is in OpenGL form [0, 128]
									//materials[matNo].shiny /= 1000.0;	//Wavefront shininess is from [0, 1000]
									//materials[matNo].shiny *= 128.0;	//So rescale for OpenGL... [0, 128]
									break;
								case 'i':	//(Ni) refraction index
									line = tokens.nextToken();
									materials[matNo].refractiveIndex = Double.parseDouble(tokens.nextToken());
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
						case 'L':	//(Lc) line color
							tokens = new StringTokenizer(line);
							try
							{
								line = tokens.nextToken();
								materials[matNo].lineColor.r = Double.parseDouble(tokens.nextToken());
								materials[matNo].lineColor.g = Double.parseDouble(tokens.nextToken());
								materials[matNo].lineColor.b = Double.parseDouble(tokens.nextToken());
								if(tokens.hasMoreTokens())
									materials[matNo].lineColor.a = Double.parseDouble(tokens.nextToken());
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
	public boolean save(String filename)
	{
		PMesh.SurfCell surf = this.surfHead;
		PMesh.PolyCell poly;
		PMesh.VertListCell vList;

		try
		{
			int i, j, k;
			BufferedWriter fout = new BufferedWriter(new FileWriter(filename));

			fout.write("#PMesh data structure needs modification to remember group name\n");
			fout.write("mtllib "+mtlFileName+"\n");
			for(i=0 ; i<vertArray.size() ; i++)
			{
				fout.write("v "+vertArray.get(i).worldPos.x+
							" "+vertArray.get(i).worldPos.y+
							" "+vertArray.get(i).worldPos.z+"\n");
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
/*	public void draw(Camera camera, GL gl, GLU glu)
	{
	
	}
*/
} // end class ObjLoaderBuffer
