package EWUPackage.loaders;

import javax.imageio.stream.*;
import java.io.*;
import java.nio.*;
import java.util.ArrayList;

import EWUPackage.scene.*;
import EWUPackage.scene.primitives.*;
import EWUPackage.utilities.*;


/**
 * This class provides a loader for loading .3ds files into the PMesh data structure.
 *
 * @version 6-Mar-2005
 */
public class ThreeDSLoader extends PMesh
{
	private static final long serialVersionUID = 1L;
	/****************************************
	Constants for loading 3DS files
	Provided courtesy of:

	The Unofficial 3DStudio 3DS File Format
	                  v1.0
	By Jeff Lewis (werewolf@worldgate.com)
	****************************************/
	public static final short PRIMARY_CHUNK         = (short)0x4D4D;
	public static final short MESH_DATA             = (short)0x3D3D;
	public static final short KEYF3DS				 = (short)0xB000;

	//Material Chunk Headers
	public static final short MATERIAL_INFO         = (short)0xAFFF;
	public static final short MATERIAL_AMBIENT      = (short)0xA001;
	public static final short MATERIAL_DIFFUSE      = (short)0xA002;
	public static final short MATERIAL_SPECULAR     = (short)0xA003;
	public static final short COLOR_24              = (short)0x0011;
	public static final short MATERIAL_TRANSPARENCY = (short)0xA005;
	public static final short SELF_ILLUMINATION     = (short)0xA084;
	public static final short MATERIAL_TEXTURE_MAP  = (short)0xA200;

	//Objects Chunk Headers (Meshes & Lights)
	public static final short NAMED_OBJECT			 = (short)0x4000;
	public static final short NAMED_TRIANGLE_OBJECT = (short)0x4100;
	public static final short TEXTURE_VERTICES		 = (short)0x4140;
	public static final short VIEW_CAMERA			 = (short)0xFFFF;
	public static final short CAMERA				 = (short)0x4700;
	public static final short CAMERA_RANGES		 = (short)0x4702;
	public static final short KEYFRAME_DATA		 = (short)0xB000;
	public static final short TRI_VERTEXL			 = (short)0x4110;
	public static final short TRI_VERTEXOPTIONS	 = (short)0x4111;
	public static final short TRI_FACEL1			 = (short)0x4120;
	public static final short TRI_MAPPINGCOORS		 = (short)0x4140;
	public static final short TRI_MAPPINGSTD		 = (short)0x4170;
	public static final short TRI_LOCAL			 = (short)0x4160;
	public static final short TRI_VISIBLE			 = (short)0x4165;
	public static final short TRI_SMOOTH			 = (short)0x4150;
	public static final short TRI_MATERIAL			 = (short)0x4130;
	public static final short OBJ_LIGHT			 = (short)0x4600;
	public static final short LIGHT_UNKNOWN		 = (short)0x465A;
	public static final short LIGHT_OFF			 = (short)0x4620;
	public static final short LIGHT_SPOT			 = (short)0x4610;
	public static final short OBJ_UNKNOWN1			 = (short)0x4710;
	public static final short OBJ_UNKNOWN2			 = (short)0x4720;

	public static final short EDIT_VIEW1			 = (short)0x7012;
	public static final short EDIT_BACKGR			 = (short)0x1200;
	public static final short EDIT_AMBIENT			 = (short)0x2100;
	public static final short EDIT_CONFIG1			 = (short)0x0100;
	public static final short EDIT_CONFIG2			 = (short)0x3D3E;
	public static final short EDIT_VIEW2			 = (short)0x7011;
	public static final short EDIT_VIEW3			 = (short)0x7020;
	public static final short EDIT_VIEW_1			 = (short)0x7001;

	public static final short EDIT_UNKNW01			 = (short)0x1100;
	public static final short EDIT_UNKNW02			 = (short)0x1201;
	public static final short EDIT_UNKNW03			 = (short)0x1300;
	public static final short EDIT_UNKNW04			 = (short)0x1400;
	public static final short EDIT_UNKNW05			 = (short)0x1420;
	public static final short EDIT_UNKNW06			 = (short)0x1450;
	public static final short EDIT_UNKNW07			 = (short)0x1500;
	public static final short EDIT_UNKNW08			 = (short)0x2200;
	public static final short EDIT_UNKNW09			 = (short)0x2201;
	public static final short EDIT_UNKNW10			 = (short)0x2210;
	public static final short EDIT_UNKNW11			 = (short)0x2300;
	public static final short EDIT_UNKNW12			 = (short)0x2302;
	public static final short EDIT_UNKNW13			 = (short)0x2000;
	//public static final short EDIT_UNKNW14			 = (short)0xAFFF;

	public static final short COLOUR_RGB			 = (short)0x0010;
	public static final short COLOUR_TRU			 = (short)0x0011;
	public static final short COLOUR_UNK			 = (short)0x0013;
/*	public static final short

*/
	public Camera camera;
	public Material mats;
	public Polys polys;
	public Verts verts;
	public int totalVerts =0;
	public int totalFaces = 0;
	public int polyChunks =0;
	public int vertChunks =0;
	public int matChunks=0;

	public SizeStack sizeStack;
	public boolean success= true;

	public double [][] texVerts;
	public double [][] vertNorms;

	public long		startFile;

	public ThreeDSLoader(Scene aScene)
	{
		super(aScene);
		sizeStack = new SizeStack();
		camera = new Camera();
		mats = null;

	}//end Contructor

	public boolean load(String filepath) throws FileNotFoundException
	{

			fileType = "3DS";
			//Set the file type to OBJ in case we ever want to later identify its source
			filePath = Utils.dirFromPath(filepath);
			//The fileName is the ENTIRE path to the file (including the filename)
			objName = Utils.fileFromPath(filepath);
			//The objName is strictly the filename to begin with

			if(read3DSFile())
			{
				if(verts == null || polys == null)
					{System.out.println("\nNO VERTEX OR POLYGON INFORMATION FOUND!\n");
					return false;}

				compileScene();
				countPolyVerts();		//Calculates how many vertices are in each polygon
				calcPolyNorms();		//Calculate all polygon normals (never rely on the file itself)
				calcVertNorms();		//Calculate all vertex normals based on polygon normals

				active = true;			//We've just created an object...so make it active!
				next = null;			//Next object in the linked list is null

				modelMat = new double [] {1.0, 0.0, 0.0, 0.0,
										  0.0, 1.0, 0.0, 0.0,
										  0.0, 0.0, 1.0, 0.0,
										  0.0, 0.0, 0.0, 1.0};

				System.out.println("\nObject loaded!\n");
				return true;
			}
			else{
				System.out.println("\nObject NOT loaded!\n");
				return false;
			}
	}//End load

	public boolean read3DSFile()
	{
		try{
		FileImageInputStream fin = openInputFile(filePath+objName);
		startFile = fin.getStreamPosition();

		while(readPrimaryChunk(fin) == 0);
		}
		catch(IOException e)
		{
			System.out.println("***Error reading from file!  IOException caught!");
			e.printStackTrace();
			System.exit(-1);
		}
		return success;
	}

	public int readPrimaryChunk(FileImageInputStream fin)throws IOException
	{
			short version;
			short chunkID = 0;
			//int chunkLen = 0;
			boolean finished = false;

			try{
				chunkID = fin.readShort();
				//chunkLen = fin.readInt();
			}
			catch(EOFException e){
				finished = true;}

			if(chunkID == PRIMARY_CHUNK && !finished)
			{	System.out.println("");System.out.println("");
				System.out.println("* 0x4D4D Start of Object");

				fin.seek(startFile);
				fin.skipBytes(28L);

				version = fin.readShort();
				System.out.println("  "+version+" Version Number ");
				if(version < 3){
					System.out.println("***Version number "+ version+" --Sorry, this is under 3, exiting...");
					success = false;sizeStack.pop();
					return(-1);}

				fin.seek(startFile);
				fin.skipBytes(2);
				readMainChunk(fin);
			}
			else
			{
				System.out.println("***Found chunkID: " + Integer.toHexString(chunkID));
				System.out.println("***File not recognized!  Bailing out...");
				return(-1);
			}
			return 0;
	}//END READ PRIMARY CHUNK

	public int readMainChunk(FileImageInputStream fin)throws IOException
	{
		boolean end = false;
		long currentPointer;
		int teller = 6;
		short chunkID = 0;
		int chunkLen = 0;

		currentPointer = fin.getStreamPosition();
		chunkLen = fin.readInt() - 2;
		sizeStack.push(chunkLen);

		while(!end){

			chunkID = fin.readShort();
			switch(chunkID){

				case KEYF3DS:
							//System.out.println("");
							System.out.println(" * KEYF3DS (0xB000) CHUNK");
							teller += readKeyFChunk(fin);
							break;

				case MESH_DATA:
							//System.out.println("");
							System.out.println(" * MESH_DATA (0x3D3D) CHUNK");
							teller += readEditChunk(fin);
							break;
				default:
							break;
				}

			teller += 2;
			if(teller >= chunkLen+2)
				end = true;

		}//END WHILE LOOP

		fin.seek(currentPointer);
		fin.skipBytes(chunkLen);

		sizeStack.pop();

		return (chunkLen);
	}//END READ MAIN CHUNK

	public int readKeyFChunk(FileImageInputStream fin)throws IOException
	{
		//short chunkID = 0;
		int chunkLen = 0;
		long currentPos = fin.getStreamPosition();

		chunkLen = fin.readInt() -2;
		sizeStack.push(chunkLen);

		//------  INSET READ KEY FRAME CHUNK -------

		sizeStack.pop();
		fin.seek(currentPos);
		fin.skipBytes(chunkLen);

		return (chunkLen);
	}//END READ KEY F CHUNK

	public int readEditChunk(FileImageInputStream fin)throws IOException
	{
		boolean endFound = false;
		long currentFilePointer;
		int tellertje = 6;
		short chunkID = 0;
		int chunkLen = 0;

		mats = new Material();
		matChunks++;
		Material curMat = mats;

		currentFilePointer = fin.getStreamPosition();
		chunkLen = fin.readInt() -2;
		sizeStack.push(chunkLen);

		while(!endFound){

			chunkID = fin.readShort();

			switch(chunkID){

				case MATERIAL_INFO:		//(0xAFFF)

							//	System.out.println("  * MATERIAL_INFO (0xAFFF)");
								curMat.nextMat = new Material();curMat = curMat.nextMat;
								tellertje += readMaterialChunk(fin, curMat);
								matChunks++;
								break;
				case EDIT_VIEW1:		//(0x7012)
							//	System.out.println("  * EDIT_VIEW1 (0x7012)");
								tellertje += readViewChunk(fin);
								break;
				case EDIT_BACKGR:		//(0x1200)
							//	System.out.println("  * EDIT_BACKGR (0x1200)");
								tellertje += readBackgroundChunk(fin);
								break;
				case EDIT_AMBIENT:		//(0x2100)
							//	System.out.println("  * EDIT_AMBIENT (0x2100)");
								tellertje += readAmbientChunk(fin);
								break;
				case NAMED_OBJECT:		//(0x4000)
							//	System.out.println("  * NAMED_OBJECT (0x4000)");
								tellertje += readObjectChunk(fin);
								break;

				case EDIT_UNKNW01://System.out.println("  * EDIT_UNKNW01");
					tellertje += readUnknownChunk(fin);break;
				case EDIT_UNKNW02://System.out.println("  * EDIT_UNKNW02");
					tellertje += readUnknownChunk(fin);break;
				case EDIT_UNKNW03://System.out.println("  * EDIT_UNKNW03");
					tellertje += readUnknownChunk(fin);break;
				case EDIT_UNKNW04://System.out.println("  * EDIT_UNKNW04");
					tellertje += readUnknownChunk(fin);break;
				case EDIT_UNKNW05://System.out.println("  * EDIT_UNKNW05");
					tellertje += readUnknownChunk(fin);break;
				case EDIT_UNKNW06://System.out.println("  * EDIT_UNKNW06");
					tellertje += readUnknownChunk(fin);break;
				case EDIT_UNKNW07://System.out.println("  * EDIT_UNKNW07");
					tellertje += readUnknownChunk(fin);break;
				case EDIT_UNKNW08://System.out.println("  * EDIT_UNKNW08");
					tellertje += readUnknownChunk(fin);break;
				case EDIT_UNKNW09://System.out.println("  * EDIT_UNKNW09");
					tellertje += readUnknownChunk(fin);break;
				case EDIT_UNKNW10://System.out.println("  * EDIT_UNKNW10");
					tellertje += readUnknownChunk(fin);break;
				case EDIT_UNKNW11://System.out.println("  * EDIT_UNKNW11");
					tellertje += readUnknownChunk(fin);break;
				case EDIT_UNKNW12://System.out.println("  * EDIT_UNKNW12");
					tellertje += readUnknownChunk(fin);break;
				case EDIT_UNKNW13://System.out.println("  * EDIT_UNKNW13");
					tellertje += readUnknownChunk(fin);break;


				case EDIT_CONFIG1://System.out.println("  * EDIT_CONFIG1");
					tellertje += readUnknownChunk(fin);break;
				case EDIT_CONFIG2://System.out.println("  * EDIT_CONFIG2");
					tellertje += readUnknownChunk(fin);break;
				case EDIT_VIEW2://System.out.println("  * EDIT_VIEW2");
					tellertje += readUnknownChunk(fin);break;
				case EDIT_VIEW3://System.out.println("  * EDIT_VIEW3");
					tellertje += readUnknownChunk(fin);break;
				case EDIT_VIEW_1://System.out.println("  * EDIT_VIEW_1");
					tellertje += readUnknownChunk(fin);break;

				default:
					//System.out.println("  * UNKNOWN CHUNK - "+chunkID+" - ");
					tellertje += readUnknownChunk(fin);break;



			}//END SWITCH
			//chunkLen = fin.readInt()-6;

			tellertje +=2;
			if(tellertje >= chunkLen+2)
				endFound = true;

		}//END WHILE

		sizeStack.pop();
		fin.seek(currentFilePointer);
		fin.skipBytes(chunkLen);
		return (chunkLen);

	}//END READ EDIT CHUNK

	public int readUnknownChunk(FileImageInputStream fin)throws IOException{

		//short chunkID = 0;
		int chunkLen = 0;
		long currentPos = fin.getStreamPosition();

		chunkLen = fin.readInt() -2;
		if(chunkLen<0)
			chunkLen = 0;
		//System.out.print(chunkLen);

		fin.seek(currentPos);
		fin.skipBytes(chunkLen);
		return (chunkLen);
	}//END READ UNKOWN CHUNK


public static final short MAT_NAME         	= (short)0xA000;
public static final short MAT_AMB         		= (short)0xA010;
public static final short MAT_DIF         		= (short)0xA020;
public static final short MAT_SPC         		= (short)0xA030;
public static final short MAT_SHINY       		= (short)0xA040;
public static final short MAT_SHN_STR   		= (short)0xA041;
public static final short MAT_TRANSPARENT  	= (short)0xA050;
public static final short MAT_TRANS_FALL	  	= (short)0xA052;
public static final short MAT_REFLECT_BLUR 	= (short)0xA053;
public static final short MAT_TYPE         	= (short)0xA100;
public static final short MAT_ILLUM        	= (short)0xA084;
public static final short MAT_TEXTURE1			= (short)0xA200;

public static final short MAT_RGB1	        	= (short)0x0011;
public static final short MAT_RGB2	        	= (short)0x0012;



	public int readMaterialChunk(FileImageInputStream fin, Material curMat)throws IOException{

		short chunkID = 0;
		int chunkLen = 0;
		long currentPos = fin.getStreamPosition();
		boolean end = false;
		int counter = 6;

		chunkLen = fin.readInt() -2;
		sizeStack.push(chunkLen);

		while(!end)
		{

			chunkID = fin.readShort();

			switch(chunkID){

				case MAT_NAME: // (0xA000)
							//System.out.println("   * MATERIAL_NAME");
							counter += readMatName(fin, curMat);
							break;
				case MAT_AMB: //
							counter += readMatAmb(fin, curMat);
							//System.out.println("   * MAT_AMB ");
							break;
				case MAT_DIF: //
							counter += readMatDif(fin, curMat);
							//System.out.println("   * MAT_DIF ");
							break;
				case MAT_SPC: // (0xA000)
							counter += readMatSpc(fin, curMat);
							//System.out.println("   * MAT_SPC ");
							break;
				case MAT_SHINY: // (0xA000)
							//System.out.println("   * MAT_SHINY");
							counter += readShinyChunk(fin, curMat);
							break;
				case MAT_SHN_STR: // (0xA000)
							//System.out.println("   * MAT_SHN_STR");
							counter += readShnStrChunk(fin, curMat);
							break;
				case MAT_TRANSPARENT: // (0xA000)
							//System.out.println("   * MAT_TRANSPARENT");
							counter += readTransparentChunk(fin,curMat);
							break;
				case MAT_TRANS_FALL: // (0xA000)
							//System.out.println("   * MAT_TRANS_FALL");
							counter += readTrnsFallChunk(fin,curMat);
							break;
				case MAT_REFLECT_BLUR: // (0xA000)
							//System.out.println("   * MAT_REFLECT_BLUR");
							counter += readReflectionChunk(fin,curMat);
							break;
				case MAT_TYPE: // (0xA000)
							//System.out.println("   * MAT_TYPE");
							counter += readTypeChunk(fin,curMat);
							break;
				case MAT_ILLUM: // (0xA000)
							//System.out.println("   * MAT_ILLUM");
							counter += readIllumChunk(fin,curMat);
							break;
				case MAT_TEXTURE1:
							//System.out.println("   * MAT_ILLUM");
							counter += readTextureChunk(fin,curMat);
							break;
				default:
							break;
			}//END SWITCH

			counter +=2;
			if(counter >= chunkLen+2)
				end = true;

		}//END WHILE

		sizeStack.pop();
		fin.seek(currentPos);
		fin.skipBytes(chunkLen);
		return (chunkLen);
	}//END READ UNKOWN CHUNK

	public static final short MAT_TXT_AMNT	        	= (short)0x0030;
	public static final short MAT_TXT_NAME	        	= (short)0xA300;
	public static final short MAT_TXT_OPTION        	= (short)0xA351;
	public static final short MAT_TXT_FILTER_BLUR     	= (short)0xA353;
	public static final short MAT_TXT_U_SCALE	     	= (short)0xA354;
	public static final short MAT_TXT_V_SCALE     		= (short)0xA356;
	public static final short MAT_TXT_U_OFFSET     	= (short)0xA358;
	public static final short MAT_TXT_V_OFFSET     	= (short)0xA35A;
	public static final short MAT_TXT_ROTATE_ANGLE    	= (short)0xA35C;

	public int readTextureChunk(FileImageInputStream fin,Material curMat)throws IOException{

		short chunkID = 0;
		int chunkLen = 0;
		int tempLen;
		int count =6;
		boolean end = false;
		long currentPos = fin.getStreamPosition();

		//short amountOf;
		@SuppressWarnings("unused")
		short options;
		@SuppressWarnings("unused")
		float filterBlur,scaleU, scaleV, offsetU, offsetV, rotateAngle;

		chunkLen = fin.readInt() -2;
		sizeStack.push(chunkLen);
		if(chunkLen<0)
			chunkLen = 0;

		while(!end)
		{
			chunkID = fin.readShort();

			switch(chunkID)
			{
				case MAT_TXT_AMNT:
						//System.out.println("    * MAT_TXT_AMNT");
						tempLen = fin.readInt()-2;
						curMat.texPercent = fin.readShort();
						//amountOf = curMat.texPercent;
						count +=tempLen;
						break;

				case MAT_TXT_NAME:
						//System.out.println("    * MAT_TXT_NAME");
						count += readMatFileName(fin,curMat);
						break;

				case MAT_TXT_OPTION:
						//System.out.println("    * MAT_TXT_OPTION");
						tempLen = fin.readInt()-2;
						options = fin.readShort();
						count +=tempLen;
						break;

				case MAT_TXT_FILTER_BLUR:
						//System.out.println("    * MAT_TXT_FILTER_BLUR");
						tempLen = fin.readInt()-2;
						filterBlur = fin.readFloat();
						count +=tempLen;
						break;

				case MAT_TXT_U_SCALE:
						//System.out.println("    * MAT_TXT_U_SCALE");
						tempLen = fin.readInt()-2;
						scaleU = fin.readFloat();
						count +=tempLen;
						break;

				case MAT_TXT_V_SCALE:
						//System.out.println("    * MAT_TXT_V_SCALE");
						tempLen = fin.readInt()-2;
						scaleV = fin.readFloat();
						count +=tempLen;
						break;

				case MAT_TXT_U_OFFSET:
						//System.out.println("    * MAT_TXT_U_OFFSET");
						tempLen = fin.readInt()-2;
						offsetU = fin.readFloat();
						count +=tempLen;
						break;

				case MAT_TXT_V_OFFSET:
						//System.out.println("    * MAT_TXT_V_OFFSET");
						tempLen = fin.readInt()-2;
						offsetV = fin.readFloat();
						count +=tempLen;
						break;


				case MAT_TXT_ROTATE_ANGLE:
						//System.out.println("    * MAT_TXT_ROTATE_ANGLE");
						tempLen = fin.readInt()-2;
						rotateAngle = fin.readFloat();
						count +=tempLen;
						break;

				default:
						break;
			}

			count+=2;
			if(count>= chunkLen)
				end = true;
		}

		sizeStack.pop();
		fin.seek(currentPos);
		fin.skipBytes(chunkLen);
		return (chunkLen);
	}

	public int[] readRGB(FileImageInputStream fin)throws IOException{

		int [] RGB = new int[]{0,0,0};
		long currentPos = fin.getStreamPosition();
		int tempChunkLen = fin.readInt()-2;

		RGB[0] += 0xFF&fin.readByte();
		RGB[1] += 0xFF&fin.readByte();
		RGB[2] += 0xFF&fin.readByte();

		fin.seek(currentPos);
		fin.skipBytes(tempChunkLen);
		return RGB;
	}

	public int readMatFileName(FileImageInputStream fin, Material curMat)throws IOException{

		//short chunkID = 0;
		int chunkLen = 0;
		long currentPos = fin.getStreamPosition();

		chunkLen = fin.readInt() -2;
		if(chunkLen<0)
			chunkLen = 0;

		curMat.txtName = "";

			int temp = fin.readByte();
			while(temp != 0)
			{
				curMat.txtName += (char)temp;
				temp = fin.readByte();
			}

		fin.seek(currentPos);
		fin.skipBytes(chunkLen);
		return (chunkLen);
	}//END READ UNKOWN CHUNK

	public int readMatName(FileImageInputStream fin, Material curMat)throws IOException{

		//short chunkID = 0;
		int chunkLen = 0;
		long currentPos = fin.getStreamPosition();

		chunkLen = fin.readInt() -2;
		if(chunkLen<0)
			chunkLen = 0;

		curMat.name = "";

			int temp = fin.readByte();
			while(temp != 0)
			{
				curMat.name += (char)temp;
				temp = fin.readByte();
			}

		fin.seek(currentPos);
		fin.skipBytes(chunkLen);
		return (chunkLen);
	}//END READ UNKOWN CHUNK

	public int readMatAmb(FileImageInputStream fin, Material curMat)throws IOException{

		short chunkID = 0;
		int chunkLen = 0;
		//int tempChunkLen =0;
		int []rgb = new int[] {0,0,0};
		long currentPos = fin.getStreamPosition();

		chunkLen = fin.readInt() -2;
		if(chunkLen<0)
			chunkLen = 0;

		chunkID = fin.readShort();

		if(chunkID == MAT_RGB1)
			rgb = readRGB(fin);

		chunkID = fin.readShort();
		if(chunkID == MAT_RGB2)
			readUnknownChunk(fin);

			curMat.ambient.r = ((double)(rgb[0]))/255.0;	//The 0xFF is necessary to prevent 2-compliment from propagating left during the casting
			curMat.ambient.g = ((double)(rgb[1]))/255.0;	//The 0xFF is necessary to prevent 2-compliment from propagating left during the casting
			curMat.ambient.b = ((double)(rgb[2]))/255.0;	//The 0xFF is necessary to prevent 2-compliment from propagating left during the casting


		fin.seek(currentPos);
		fin.skipBytes(chunkLen);
		return (chunkLen);
	}//END READ UNKOWN CHUNK

	public int readMatDif(FileImageInputStream fin, Material curMat)throws IOException{

		short chunkID = 0;
		int chunkLen = 0;
		//int tempChunkLen =0;
		int []rgb = new int[] {0,0,0};
		long currentPos = fin.getStreamPosition();

		chunkLen = fin.readInt() -2;
		if(chunkLen<0)
			chunkLen = 0;

		chunkID = fin.readShort();

		if(chunkID == MAT_RGB1)
			rgb = readRGB(fin);

		chunkID = fin.readShort();
				if(chunkID == MAT_RGB2)
					readUnknownChunk(fin);


			curMat.diffuse.r = ((double)(rgb[0]))/255.0;	//The 0xFF is necessary to prevent 2-compliment from propogating left during the casting
			curMat.diffuse.g = ((double)(rgb[1]))/255.0;	//The 0xFF is necessary to prevent 2-compliment from propogating left during the casting
			curMat.diffuse.b = ((double)(rgb[2]))/255.0;	//The 0xFF is necessary to prevent 2-compliment from propogating left during the casting

		fin.seek(currentPos);
		fin.skipBytes(chunkLen);
		return (chunkLen);
	}//END READ UNKOWN CHUNK

	public int readMatSpc(FileImageInputStream fin, Material curMat)throws IOException{

		short chunkID = 0;
		int chunkLen = 0;
		//int tempChunkLen =0;
		int []rgb = new int[] {0,0,0};
		long currentPos = fin.getStreamPosition();

		chunkLen = fin.readInt() -2;
		if(chunkLen<0)
			chunkLen = 0;

		chunkID = fin.readShort();

		if(chunkID == MAT_RGB1)
			rgb = readRGB(fin);

		chunkID = fin.readShort();
		if(chunkID == MAT_RGB2)
			readUnknownChunk(fin);

			curMat.specular.r = ((double)(rgb[0]))/255.0;	//The 0xFF is necessary to prevent 2-compliment from propogating left during the casting
			curMat.specular.g = ((double)(rgb[1]))/255.0;	//The 0xFF is necessary to prevent 2-compliment from propogating left during the casting
			curMat.specular.b = ((double)(rgb[2]))/255.0;	//The 0xFF is necessary to prevent 2-compliment from propogating left during the casting

		fin.seek(currentPos);
		fin.skipBytes(chunkLen);
		return (chunkLen);
	}//END READ UNKOWN CHUNK

	public int readShinyChunk(FileImageInputStream fin, Material curMat)throws IOException{

		@SuppressWarnings("unused")
		short chunkID = 0;
		int chunkLen = 0;
		long currentPos = fin.getStreamPosition();

		chunkLen = fin.readInt() -2;
		if(chunkLen<0)
			chunkLen = 0;

		chunkID = fin.readShort();
		@SuppressWarnings("unused")
		int temp = fin.readInt() -2;

		curMat.shinny = fin.readShort();

		fin.seek(currentPos);
		fin.skipBytes(chunkLen);
		return (chunkLen);
	}//END READ UNKOWN CHUNK

	public int readShnStrChunk(FileImageInputStream fin, Material curMat)throws IOException{

		@SuppressWarnings("unused")
		short chunkID = 0;
		int chunkLen = 0;
		long currentPos = fin.getStreamPosition();

		chunkLen = fin.readInt() -2;
		if(chunkLen<0)
			chunkLen = 0;

		chunkID = fin.readShort();
		@SuppressWarnings("unused")
		int temp = fin.readInt() -2;

		curMat.shinnyStrength = fin.readShort();

		fin.seek(currentPos);
		fin.skipBytes(chunkLen);
		return (chunkLen);
	}//END READ UNKOWN CHUNK

	public int readTransparentChunk(FileImageInputStream fin, Material curMat)throws IOException{

		@SuppressWarnings("unused")
		short chunkID = 0;
		int chunkLen = 0;
		long currentPos = fin.getStreamPosition();

		chunkLen = fin.readInt() -2;
		if(chunkLen<0)
			chunkLen = 0;

		chunkID = fin.readShort();
		@SuppressWarnings("unused")
		int temp = fin.readInt() -2;

		curMat.trans = fin.readShort();

		fin.seek(currentPos);
		fin.skipBytes(chunkLen);
		return (chunkLen);
	}//END READ UNKOWN CHUNK

	public int readTrnsFallChunk(FileImageInputStream fin, Material curMat)throws IOException{

		@SuppressWarnings("unused")
		short chunkID = 0;
		int chunkLen = 0;
		long currentPos = fin.getStreamPosition();

		chunkLen = fin.readInt() -2;
		if(chunkLen<0)
			chunkLen = 0;

		chunkID = fin.readShort();
		@SuppressWarnings("unused")
		int temp = fin.readInt() -2;

		curMat.transFall = fin.readShort();

		fin.seek(currentPos);
		fin.skipBytes(chunkLen);
		return (chunkLen);
	}//END READ UNKOWN CHUNK

	public int readReflectionChunk(FileImageInputStream fin, Material curMat)throws IOException{

		@SuppressWarnings("unused")
		short chunkID = 0;
		int chunkLen = 0;
		long currentPos = fin.getStreamPosition();

		chunkLen = fin.readInt() -2;
		if(chunkLen<0)
			chunkLen = 0;

		chunkID = fin.readShort();
		@SuppressWarnings("unused")
		int temp = fin.readInt() -2;

		curMat.reflect = fin.readShort();

		fin.seek(currentPos);
		fin.skipBytes(chunkLen);
		return (chunkLen);
	}//END READ UNKOWN CHUNK

	public int readTypeChunk(FileImageInputStream fin, Material curMat)throws IOException{

		//short chunkID = 0;
		int chunkLen = 0;
		long currentPos = fin.getStreamPosition();

		chunkLen = fin.readInt() -2;
		if(chunkLen<0)
			chunkLen = 0;

		curMat.type = fin.readShort();

		fin.seek(currentPos);
		fin.skipBytes(chunkLen);
		return (chunkLen);
	}//END READ UNKOWN CHUNK

	public int readIllumChunk(FileImageInputStream fin, Material curMat)throws IOException{

		@SuppressWarnings("unused")
		short chunkID = 0;
		int chunkLen = 0;
		long currentPos = fin.getStreamPosition();

		chunkLen = fin.readInt() -2;
		if(chunkLen<0)
			chunkLen = 0;

		chunkID = fin.readShort();
		@SuppressWarnings("unused")
		int temp = fin.readInt() -2;

		curMat.selfIllum = fin.readShort();

		fin.seek(currentPos);
		fin.skipBytes(chunkLen);
		return (chunkLen);
	}//END READ UNKOWN CHUNK

	public int readViewChunk(FileImageInputStream fin)throws IOException{

		short chunkID = 0;
		int chunkLen = 0;
		boolean end = false;
		long currentPos = fin.getStreamPosition();
		int count = 6;

		chunkLen = fin.readInt() -2;
		sizeStack.push(chunkLen);

		while(!end)
		{
			chunkID = fin.readShort();

			switch(chunkID)
			{
				case EDIT_VIEW1: //(0x7012)
								//System.out.println("   * EDIT_VIEW1");
								count += readViewPortChunk(fin);
								break;
				case EDIT_VIEW2: //(0x7011)
								//System.out.println("   * EDIT_VIEW2");
								count += readViewPortChunk(fin);
								break;
				case EDIT_VIEW3: //(0x7020)
								//System.out.println("   * EDIT_VIEW3");
								count += readViewPortChunk(fin);
								break;
				default:
						break;
			}
			count += 2;
			if(count>=chunkLen+2)
				end = true;
		}

		sizeStack.pop();
		fin.seek(currentPos);
		fin.skipBytes(chunkLen);
		return (chunkLen);
	}//END READ VIEW CHUNK

	public int readViewPortChunk(FileImageInputStream fin)throws IOException
	{
		//short chunkID = 0;
		int chunkLen = 0;
		//boolean end = false;
		long currentPos = fin.getStreamPosition();
		//int count = 6;

		chunkLen = fin.readInt() -2;
		if(chunkLen<0)
			chunkLen = 0;

		short port;
		@SuppressWarnings("unused")
		short attributes = fin.readShort();

		//eats up 5 shorts to get to port
		for(int i=0;i<5;i++) fin.readShort();

		port = fin.readShort();
		if((port == 0xFFFF) || (port == 0x0000))
		{
			// Read in camera name here if desired

			port = VIEW_CAMERA;
		}

		fin.seek(currentPos);
		fin.skipBytes(chunkLen);
		return (chunkLen);
	}//END READ VIEW CHUNK

	public int readBackgroundChunk(FileImageInputStream fin)throws IOException
	{
		short chunkID = 0;
		int chunkLen = 0;
		boolean end = false;
		int count=6;
		long currentPos = fin.getStreamPosition();

		chunkLen = fin.readInt() -2;
		sizeStack.push(chunkLen);

		while(!end)
		{
			chunkID = fin.readShort();

			switch(chunkID)
			{
				case COLOUR_RGB:
							//System.out.println("   * COLOUR_RGB");
							count += readUnknownChunk(fin);
							//CAN SET TO READ THE RGB BG COLOUR
							break;
				case COLOUR_TRU:
							//System.out.println("   * COLOUR_TRU");
							count += readUnknownChunk(fin);
							//CAN SET TO READ THE TRU BG COLOUR
							break;
				default:
							break;
			}//END SWITCH

			count += 2;
			if(count >= chunkLen+2)
				end = true;

		}//END WHILE

		sizeStack.pop();
		fin.seek(currentPos);
		fin.skipBytes(chunkLen);
		return (chunkLen);
	}//END READ BAVKGROUND CHUNK

	public int readAmbientChunk(FileImageInputStream fin)throws IOException
	{
		short chunkID = 0;
		int chunkLen = 0;
		boolean end = false;
		int count=6;
		long currentPos = fin.getStreamPosition();

		chunkLen = fin.readInt() -2;
		sizeStack.push(chunkLen);

		while(!end)
		{
			chunkID = fin.readShort();

			switch(chunkID)
			{
				case COLOUR_RGB:
							//System.out.println("   * COLOUR_RGB");
							count += readUnknownChunk(fin);
							//CAN SET TO READ THE RGB BG COLOUR
							break;
				case COLOUR_TRU:
							//System.out.println("   * COLOUR_TRU");
							count += readUnknownChunk(fin);
							//CAN SET TO READ THE TRU BG COLOUR
							break;
				default:
							break;
			}//END SWITCH

			count += 2;
			if(count >= chunkLen+2)
				end = true;

		}//END WHILE

		sizeStack.pop();
		fin.seek(currentPos);
		fin.skipBytes(chunkLen);
		return (chunkLen);
	}//END READ AMBIENT CHUNK

	public int readObjectChunk(FileImageInputStream fin)throws IOException
	{
		short chunkID = 0;
		int chunkLen = 0;
		boolean end = false;
		int count=6;
		long currentPos = fin.getStreamPosition();

		chunkLen = fin.readInt() -2;
		sizeStack.push(chunkLen);

		String name = readName(fin);
		if(name == "")
			name = "NO_NAME_FOUND";

		while(!end)
		{
			chunkID = fin.readShort();

			switch(chunkID)
			{
				case NAMED_TRIANGLE_OBJECT://(0x4100)
							//System.out.println("   * NAMED_TRIANGLE_OBJECT");
							count += readObjMeshChunk(fin);
							break;
				case CAMERA: //(0x4700)
							//System.out.println("   * CAMERA");
							count += readCameraChunk(fin, name);
							break;
				case OBJ_LIGHT:
							//System.out.println("   * OBJ_LIGHT");
							count += readLightChunk(fin);
							break;
				case OBJ_UNKNOWN1:
							//System.out.println("   * OBJ_UNKNOWN1");
							count += readUnknownChunk(fin);
							break;
				case OBJ_UNKNOWN2:
							//System.out.println("   * OBJ_UNKNOWN2");
							count += readUnknownChunk(fin);
							break;
				default:
							break;
			}//END SWITCH

			count += 2;
			if(count >= chunkLen+2)
				end = true;

		}//END WHILE

		sizeStack.pop();
		fin.seek(currentPos);
		fin.skipBytes(chunkLen);
		return (chunkLen);
	}//END READ OBJECT CHUNK


	public int readCameraChunk(FileImageInputStream fin, String name)throws IOException
	{
		//short chunkID = 0;
		int chunkLen = 0;
		//boolean end = false;
		long currentPos = fin.getStreamPosition();
		//int count = 6;

		chunkLen = fin.readInt() -2;
		if(chunkLen<0)
			chunkLen = 0;

		camera = new Camera();

		camera.name = name+="_Camera";
		camera.eye[0] = fin.readFloat();
		camera.eye[1] = fin.readFloat();
		camera.eye[2] = fin.readFloat();
		camera.focus[0] = fin.readFloat();
		camera.focus[1] = fin.readFloat();
		camera.focus[2] = fin.readFloat();
		camera.rotation = fin.readFloat();
		camera.lens = fin.readFloat();

		fin.skipBytes(6);
		camera.near = fin.readFloat();
		camera.far = fin.readFloat();

		fin.seek(currentPos);
		fin.skipBytes(chunkLen);
		return (chunkLen);
	}//END READ CAMERA CHUNK

	public int readLightChunk(FileImageInputStream fin )throws IOException
	{
		short chunkID = 0;
		int chunkLen = 0;
		boolean end = false;
		long currentPos = fin.getStreamPosition();
		int count = 6;

		chunkLen = fin.readInt() -2;
		sizeStack.push(chunkLen);

		while(!end)
		{
			chunkID = fin.readShort();

			switch(chunkID)
			{
				case COLOUR_RGB:
						//System.out.println("    * COLOUR_RGB");
						count += readUnknownChunk(fin);
						//CAN SET TO READ THE RGB BG COLOUR
						break;

				case COLOUR_TRU:
						//System.out.println("    * COLOUR_TRU");
						count += readUnknownChunk(fin);
						//CAN SET TO READ THE TRU BG COLOUR
						break;

				case LIGHT_UNKNOWN:
						//System.out.println("    * LIGHT_UNKNOWN");
						count += readUnknownChunk(fin);
						break;

				case LIGHT_SPOT:
						//System.out.println("    * LIGHT_SPOT");
						count += readUnknownChunk(fin);
						//CAN SET TO READ SPOT CHUNK
						break;

				case LIGHT_OFF:
						//System.out.println("    * LIGHT_OFF");
						count += readUnknownChunk(fin);
						//CAN SET TO READ BOOLEAN CHUNK
						break;
				default:
						break;
			}

			count +=2;
			if(count >= chunkLen+2)
				end = true;

		}//END WHILE

		sizeStack.pop();
		fin.seek(currentPos);
		fin.skipBytes(chunkLen);
		return (chunkLen);
	}//END READ LIGHT CHUNK

	public int readObjMeshChunk(FileImageInputStream fin)throws IOException
	{
		short chunkID = 0;
		int chunkLen = 0;
		boolean end = false;
		int count=6;
		long currentPos = fin.getStreamPosition();

		chunkLen = fin.readInt() -2;
		sizeStack.push(chunkLen);

		while(!end)
		{
			chunkID = fin.readShort();

			switch(chunkID)
			{
				case TRI_VERTEXL:
							//System.out.println("    * TRI_VERTEXL");
							count += readVerticesChunk(fin);
							vertChunks++;
							break;
				case TRI_FACEL1:
							//System.out.println("    * TRI_FACEL1");
							count += readFacesChunk(fin);
							polyChunks++;
							break;
				case TRI_VERTEXOPTIONS:
							//System.out.println("    * TRI_VERTEXOPTIONS");
							count += readUnknownChunk(fin);
							break;
				case TRI_LOCAL:
							//System.out.println("    * TRI_LOCAL");
							count += readTranslationChunk(fin);
							break;
				case TRI_VISIBLE:
							//System.out.println("    * TRI_VISIBLE");
							count += readUnknownChunk(fin);
							//CAN SET TO READ IF OBJ IS VISIBLE OR NOT
							//READ A BOOLEAN CHUNK
							break;
				default:
							break;
			}//END SWITCH

			count += 2;
			if(count >= chunkLen+2)
				end = true;

		}//END WHILE

		sizeStack.pop();
		fin.seek(currentPos);
		fin.skipBytes(chunkLen);
		return (chunkLen);
	}//END READ OBJ MESH CHUNK

	public int readTranslationChunk(FileImageInputStream fin)throws IOException
	{
		//short chunkID = 0;
		int chunkLen = 0;
		//boolean end = false;
		long currentPos = fin.getStreamPosition();
		//int count = 6;

		chunkLen = fin.readInt() -2;
		if(chunkLen<0)
			chunkLen = 0;

		//NOT STORED IN DATA - LOST AFTER READ - EASILY CHANGABLE
		float [][]transMatrix = new float[4][4];

		for(int m=0;m<4;m++)
			for(int n=0;n<3;n++)
				transMatrix[m][n] = fin.readFloat();

		transMatrix[0][3] = 0;
		transMatrix[1][3] = 0;
		transMatrix[2][3] = 0;
		transMatrix[3][3] = 1;

		fin.seek(currentPos);
		fin.skipBytes(chunkLen);
		return (chunkLen);
	}//END READ TRANSLATION CHUNK

	public int readFacesChunk(FileImageInputStream fin)throws IOException
	{
		short chunkID = 0;
		int chunkLen = 0;
		//boolean end = false;
		long currentPos = fin.getStreamPosition();
		int faces = 0;
		int temp_diff;
		int counter = 6;
		Polys curPolySet;

		chunkLen = fin.readInt() -2;
		if(chunkLen<0)
			chunkLen = 0;

		if(polys == null){
			polys = new Polys();
			curPolySet = polys;}
		else{
			curPolySet = polys;
			while(curPolySet.nextPoly != null)
				curPolySet = curPolySet.nextPoly;
			curPolySet.nextPoly = new Polys();
			curPolySet = curPolySet.nextPoly;
			}

		faces = fin.readShort(); counter +=2;

		//System.out.println("       Faces - "+faces);

		totalFaces += faces;
		curPolySet.numFace = faces;
		curPolySet.polys = new int[faces][6]; // a,b,c,Diff  (Diff = AB: BC:  CA:)

		for(int i=0;i<faces;i++)
		{
			//polys[i] = new int[6];
			curPolySet.polys[i][0] = fin.readShort();
			curPolySet.polys[i][1] = fin.readShort();
			curPolySet.polys[i][2] = fin.readShort();
			temp_diff = fin.readShort() & 0x000F;
			curPolySet.polys[i][3] = (temp_diff & 0x0004);
			curPolySet.polys[i][4] = (temp_diff & 0x0002);
			curPolySet.polys[i][5] = (temp_diff & 0x0001);

			counter += 8;
		}

		boolean ended = false;
		MatIndex curMI = null;

		while(!ended)
		{
			chunkID = fin.readShort();

			switch(chunkID)
			{
				case TRI_SMOOTH:
							//System.out.println("       TRI_SMOOTH DATA");
							counter += readSmoothChunk(fin, curPolySet);
							break;
				case TRI_MATERIAL:
							if(curPolySet.matIndex == null){
								curPolySet.matIndex = new MatIndex();
								curMI = curPolySet.matIndex;}
							else{
								curMI.nextMI = new MatIndex();
								curMI = curMI.nextMI;}
							curPolySet.numMats++;
							//System.out.println("       TRI_MATERIAL DATA");
							counter += readSurfMatChunk(fin, curMI);
							break;
				default:
							break;
			}

			counter +=2;
			if(counter >= chunkLen)
				ended = true;
		}


		fin.seek(currentPos);
		fin.skipBytes(chunkLen);
		return (chunkLen);
	}//END READ FACES CHUNK

	public int readSmoothChunk(FileImageInputStream fin, Polys polySet)throws IOException
	{
		//short chunkID = 0;
		int chunkLen = 0;
		//boolean end = false;
		long currentPos = fin.getStreamPosition();

		chunkLen = fin.readInt() -2;
		if(chunkLen<0)
			chunkLen = 0;

		//System.out.println("        **Length "+chunkLen);

		int temp = (chunkLen -6) / 4;
		if(temp <0) temp =0;

		polySet.smooth = new int[temp];

		for(int i=0;i<temp;i++)
			polySet.smooth[i] = fin.readInt();

		fin.seek(currentPos);
		fin.skipBytes(chunkLen);
		return (chunkLen);
	}//END READ SMOOTH CHUNK

	public int readSurfMatChunk(FileImageInputStream fin, MatIndex curMI)throws IOException
	{
			//short chunkID = 0;
			int chunkLen = 0;

			long currentPos = fin.getStreamPosition();

			chunkLen = fin.readInt() -2;
			if(chunkLen<0)
				chunkLen = 0;

			curMI.matName = "";
			int temp = fin.readByte();
			while(temp != 0)
			{
				curMI.matName += (char)temp;
				temp = fin.readByte();
			}

			curMI.faces = fin.readShort();

			//System.out.println("        **MATERIAL INDEX "+curMI.matName+" - "+curMI.faces);

			fin.seek(currentPos);
			fin.skipBytes(chunkLen);
			return (chunkLen);
	}//END READ SURFACE MATERIAL CHUNK

	public int readVerticesChunk(FileImageInputStream fin)throws IOException
	{
		//short chunkID = 0;
		int chunkLen = 0;
		//boolean end = false;
		long currentPos = fin.getStreamPosition();
		//int count = 6;
		Verts curVert;
		int verticies;

		chunkLen = fin.readInt() -2;
		if(chunkLen<0)
			chunkLen = 0;

		if(verts == null){
			verts = new Verts();
			curVert = verts;}
		else{
			curVert = verts;
			while(curVert.nextVert != null)
				curVert = curVert.nextVert;
			curVert.nextVert = new Verts();
			curVert = curVert.nextVert;}

		verticies = fin.readShort();

		curVert.numVerts = verticies;
		totalVerts += curVert.numVerts;
		curVert.verts = new float[curVert.numVerts][3];

		for(int i=0;i<curVert.numVerts;i++)
		{
			curVert.verts[i][0] = fin.readFloat();
			curVert.verts[i][1] = fin.readFloat();
			curVert.verts[i][2] = fin.readFloat();
		}

		fin.seek(currentPos);
		fin.skipBytes(chunkLen);
		return (chunkLen);
	}//END READ TRANSLATION CHUNK


	public String readName(FileImageInputStream fin)throws IOException
	{
		String name = "";
		int num =0;
		int temp = fin.readByte();
		while(temp != 0 && num<12)
		{
			name += (char)temp;
			temp = fin.readByte();
			num++;
		}
		return name;
	}

	public void compileScene()
	{
		numVerts = totalVerts;
		vertArray = new ArrayList<VertCell>();
		int num=0;int pass=0;
		int[] offset = new int[vertChunks];

		double xH,yH,zH,xL,yL,zL;//, xC,yC,zC;

		xH = xL= verts.verts[0][0];
		yH = yL= verts.verts[0][1];
		zH = zL= verts.verts[0][2];

		Verts curVert = verts;
		Polys curPoly = polys;
		System.out.println("\n\n**COMPILING SCENE**\n  Total verts:"+totalVerts);
		System.out.println("  CHUNK COUNTS -- Vert:"+vertChunks+" -- Poly:"+polyChunks+" -- Mat:"+matChunks);

		while(curVert != null )//&& curPoly != null)
		{
			offset[pass] = num;
			for(int i=0;i<curVert.numVerts;i++)
			{
				//if(num < numVerts){
				vertArray.add(num,  new VertCell());
				vertArray.get(num).worldPos = new Double3D(curVert.verts[i][0],curVert.verts[i][1],curVert.verts[i][2]);

				if(vertArray.get(num).worldPos.x > xH)
					xH = vertArray.get(num).worldPos.x;
				if(vertArray.get(num).worldPos.y > yH)
					yH = vertArray.get(num).worldPos.y;
				if(vertArray.get(num).worldPos.z > zH)
					zH = vertArray.get(num).worldPos.z;

				if(vertArray.get(num).worldPos.x < xL)
					xL = vertArray.get(num).worldPos.x;
				if(vertArray.get(num).worldPos.y < yL)
					yL = vertArray.get(num).worldPos.y;
				if(vertArray.get(num).worldPos.x < zL)
					zL = vertArray.get(num).worldPos.z;

				vertArray.get(num).polys = new PolyListCell();
				num++;
				//}
			}
			pass++;
			curVert = curVert.nextVert;
		}

		//xC = (xH + xL) / 2.0f;
		//yC = (yH + yL) / 2.0f;
		//zC = (zH + zL) / 2.0f;

		center = new Double3D((xH + xL) / 2.0f,(yH + yL) / 2.0f,(zH + zL) / 2.0f);

		pass=0;
		PolyCell curPolyCell;
		VertListCell curVertListCell;
		//PolyListCell curPolyListCell;
		SurfCell curSurf = surfHead;
		surfHead = new SurfCell("temp");
		curSurf = surfHead;
		MatIndex MI;


		while(curPoly != null)
		{
			int subOffset = 0;
			MI = curPoly.matIndex;

			while(MI != null)
			{
					curSurf.numPoly = MI.faces;
					curSurf.polyHead = new PolyCell();
					curPolyCell = curSurf.polyHead;


						for(int j=0;j<MI.faces;j++)
						{
							curPolyCell.numVerts = 3;
							curPolyCell.vert = new VertListCell();
							curVertListCell = curPolyCell.vert;
							curVertListCell.next = new VertListCell();
							curVertListCell.next.next = new VertListCell();

							curVertListCell.vert = curPoly.polys[j+subOffset][0]+offset[pass];
							curVertListCell.next.vert = curPoly.polys[j+subOffset][1]+offset[pass];
							curVertListCell.next.next.vert = curPoly.polys[j+subOffset][2]+offset[pass];

							vertArray.get(curVertListCell.vert).polys.poly = curPolyCell;
							vertArray.get(curVertListCell.next.vert).polys.poly = curPolyCell;
							vertArray.get(curVertListCell.next.next.vert).polys.poly = curPolyCell;

							if(j+1 <MI.faces){
								curPolyCell.next = new PolyCell();
								curPolyCell = curPolyCell.next;}
						}

				Material temp = mats;
				curSurf.material = 0;

				for(int f=0;f<matChunks;f++)
				{
					if(MI.matName.compareTo(temp.name) == 0)
						curSurf.material = f;
					temp = temp.nextMat;
				}

				//GENERATE NEW SURFACE AT END OF LOOP
				if(MI.nextMI != null){
					curSurf.next = new SurfCell("temp1");
					curSurf = curSurf.next;}

				//System.out.println("**Surface Material - "+MI.matName);
				subOffset += MI.faces;
				MI = MI.nextMI;
			}

			if(curPoly.nextPoly != null){
				curSurf.next = new SurfCell("temp3");
				curSurf = curSurf.next;}

			pass++;
			curPoly = curPoly.nextPoly;

		}//END WHILE


		Material curMat = mats;

		materials = new MaterialCell[matChunks];

			for(int k=0;k<matChunks;k++)
			{

				materials[k] = new MaterialCell();
				materials[k].materialName = curMat.name;
				materials[k].ka = new DoubleColor(curMat.ambient.r,curMat.ambient.g,curMat.ambient.b,1.0);
				materials[k].kd = new DoubleColor(curMat.diffuse.r,curMat.diffuse.g,curMat.diffuse.b, 1.0);
				materials[k].ks = new DoubleColor(curMat.specular.r,curMat.specular.g,curMat.specular.b, 1.0);
				materials[k].shiny = curMat.shinny;
				//materials[k].tr = curMat.trans;

				if(curMat.txtName != null){
					materials[k].mapKa = curMat.txtName;
					materials[k].mapKd = curMat.txtName;
					materials[k].mapKs = curMat.txtName;
					materials[k].mapD = curMat.txtName;}

				curMat = curMat.nextMat;
			}

	}//END COMPILE SCENE


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

////////////////////////////////////////////////////////////////////
//////////////  EXTRA CLASS FILES //////////////////////////////
/////////////////////////////////////////////////////////////////////


class Material
{
	protected String name;			//Material name
	protected DoubleColor ambient;		//Color as an array of bytes: red, green, blue
	protected DoubleColor diffuse;		//Color as an array of bytes: red, green, blue
	protected DoubleColor specular;	//Color as an array of bytes: red, green, blue
	protected float shinny;			//Percentage (originally a short in the 3ds file)
	protected float shinnyStrength;	//Percentage (originally a short in the 3ds file)
	protected float trans;			//Percentage (originally a short in the 3ds file)
	protected float selfIllum;		//Percentage (originally a short in the 3ds file)
	protected String txtName;		//Filename   (will need to be opened later)
	protected float texPercent;		//Percentage (originally a short in the 3ds file)
	protected Material nextMat;
	protected float transFall;
	protected float reflect;
	protected int type;

	public Material()
	{
		name = "Default Material";
		ambient = new DoubleColor(0.3, 0.4, 0.8, 1.0);
		diffuse = new DoubleColor(0.4, 0.3, 0.8, 1.0);
		specular = new DoubleColor(0.4, 0.3, 0.8, 1.0);
		shinny = 0.3f;
		shinnyStrength = 0.3f;
		trans = 0.0f;
		transFall = 0.0f;
		selfIllum = 0.0f;
		type = 1;
		texPercent = 0.0f;
		nextMat = null;
		reflect = 0.0f;
		txtName = null;
	}

	public String toString()
	{
		String output = "\n";
		output += "Name               : " + name + "\n";
		output += "Ambient            : Red = " + ambient.r + "\t  Green = " + ambient.g + "\tBlue = " + ambient.b + "\n";
		output += "Diffuse            : Red = " + diffuse.r + "\t  Green = " + diffuse.g + "\tBlue = " + diffuse.b + "\n";
		output += "Specular           : Red = " + specular.r + "\t  Green = " + specular.g + "\tBlue = " + specular.b + "\n";
		output += "Shinniness         : " + shinny + "  (strength = " + shinnyStrength + ")\n";
		output += "Transparency       : " + trans + "\n";
		output += "Trans Fall Off     : " + transFall + "\n";
		output += "Reflection Blur    : " + reflect + "\n";
		output += "Self Illum.        : " + selfIllum+"\n";
		output += "Material Type.     : " + type;

		if(txtName != null)
		{
			output += "\nTexture file name  : " + txtName + "\n";
			output += "Texture percentage : " + texPercent;
		}
		return output;
	}
}

class MatIndex
{
	protected String matName;
	protected short faces;
	protected MatIndex nextMI;

	public MatIndex()
	{
		matName = "";
		faces = 0;
		nextMI = null;
	}
}

class Polys
{
	protected int [][] polys;
	protected int numFace;
	protected Polys nextPoly;
	protected int [] smooth;
	protected MatIndex matIndex;
	protected int numMats;

	public Polys()
	{
		numFace = 0;
		numMats = 0;
		matIndex = null;
		nextPoly = null;
	}
}

class Verts
{
	protected float [][] verts;
	protected int numVerts;
	protected Verts nextVert;

	public Verts()
	{
		verts = null;
		nextVert = null;
		numVerts = 0;
	}
}

class Camera
{
	protected String name;
	protected double [] eye;
	protected double [] focus;
	protected double near;
	protected double far;
	protected double lens;
	protected double rotation;

	public Camera()
	{
		name = "";
		eye = new double [] {0.0, 0.0, 0.0};
		focus = new double [] {0.0, 0.0, 0.0};
		lens = 0.0;
		rotation = 0.0;
		near = 0.0;
		far = 0.0;
	}

	public String toString()
	{
		return "       Name   -  " + name + "\n" +
			   "       Origin -  x: " + eye[0] + "  y: " + eye[1] + "  z: " + eye[2] + "\n" +
			   "       Target -  x: " + focus[0] + "  y: " + focus[1] + "  z: " + focus[2] + "\n" +
			   "       Near   -  " + near + "\n" +
			   "       Far    -  " + far + "\n" +
			   "       Lens   -  " + lens + "\n" +
			   "       Rotate -  " + rotation;
	}
}


	public FileImageInputStream openInputFile(String fileName)
	{
		FileImageInputStream fiis = null;
		File inFile= null;
		//String filename = null;
		boolean fileNotFound = true;

		//BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));

		System.out.print("\nPlease enter the input filename: ");
		do
		{
			try
			{
				fileNotFound = false;
				System.out.println("fileName");
				//filename = stdin.readLine();
				//inFile = new File(filename);
				inFile = new File(fileName);
				fiis = new FileImageInputStream(inFile);

				fiis.setByteOrder(ByteOrder.LITTLE_ENDIAN);
			}
			catch(FileNotFoundException fnfe)
			{
				fileNotFound = true;
				System.out.println("\nThe file \"" + fileName + "\" could not be found.");
				System.out.print("Please re-enter your filename: ");
			}
			catch(Exception e)
			{
				System.out.println("\nCritical error while trying to open file.");
				e.printStackTrace();
				System.exit(-1);
			}
		}while(fileNotFound);

		return fiis;

	}

	protected class SizeStack{

		int stack[];
		int position = 1;
		int size;

		public SizeStack(){

			stack = new int[10];
			for(int f=0;f<10;f++) stack[f]=0;

			stack[0] = 100000000;//Largest Decimal Int possible
		}

		public void push(int num){

			if(num >= stack[position -1]-6)
				System.out.println("*** Bad Size ***");

			stack[position] = num;
			//System.out.println("Stack - "+position+" - "+num);

			position++;
		}

		public int pop(){

			int temp = stack[position];
			stack[position] = 0;
			position--;
			return temp;
		}
	}

} // end class ThreeDSLoader
