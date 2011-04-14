
import java.io.FileNotFoundException;
import collada.*;
import java.util.*;

public class ColladaLoader extends PMesh{

	public ColladaLoader(Scene aScene)
	{
		super(aScene);
	} // end constructor
	
	public boolean load(String filepath) throws FileNotFoundException
	{
		fileType = "DAE"; // Set the file type to OBJ in case we ever want to later identify its source
		filePath = Utils.dirFromPath(filepath); // The fileName is the ENTIRE path to the file (including the filename)
		objName = Utils.fileFromPath(filepath); // The objName is strictly the filename to begin with
		active = true;	//We've just created an object...so make it active!
		next = null;	//Next object in the linked list is null

		Collada collada = new Collada(filepath);
		
		loadEffects(collada.effects);
		loadModel(collada);
		
		//This simply loads the modelMat with an identity matrix.
		modelMat = MatrixOps.newIdentity();
		return true;
	}
	
	
	//loads the first model only from the collada file
	public void loadModel(Collada collada)
	{
		
		String sceneUrl = collada.scene.instance_visual_scene.url.substring(1);  //starting point into the collada file
		
		Nodec node = null;		//the first node in the scene
		boolean flag = true;
		String igurl;			//the starting url 
		Mesh mesh=null;			//the mesh structor to be loaded
		Triangles tempTri;		//each Triangles is a surfCell
		Vertices tempVerties=null;  //the vertices for the mesh
		
		//next 5 objects are used to help load from the collada data to the PMesh data
		VertCell tempVertCell;		
		PolyCell tempPolyCell;
		SurfCell tempSurfCell;
		VertListCell tempVertListCell;
		PolyListCell tempPolyListCell;
		
		
		int offset=0;	//the offset of each vertex
		int cur = 0;	//the curent polycell being loaded
		
		ArrayList normals = new ArrayList();  //holds the normals until they are loaded into PMesh
		Instance_Geometry instanceGeom;		  //holds the geometry data
		
		//used for keeping track of the offsets offsets
		Input vertexO=null,
		normalO=null,
		textCoordO=null,
		textTangO=null,
		textBinO=null;
		
		Library_Visual_Scenes lbs = collada.vscenes;  
		Library_Geometries lg = collada.gemetries;
		
		
		//start at the starting point and find the node we want to draw
		for(int x =0; x< lbs.scenes.size() && flag;x++)
		{
			if(((Visual_Scene)lbs.scenes.get(x)).id.matches(sceneUrl))
			{
				node = (Nodec)((Visual_Scene)lbs.scenes.get(x)).nodes.get(0);
				flag = false;
			}
		}
		
		if(flag || node == null)
		{
			System.out.println("could not load object");
			return;
		}
		
		instanceGeom  = (Instance_Geometry)node.instance_geometry.get(0);
		igurl = instanceGeom.url.substring(1);
		
		flag = true;
				
		//get the mesh data from the node that we are drawing
		for(int x =0; x < lg.geometry.size() && flag;x++ )
		{
			if(((Geometry)lg.geometry.get(x)).id.matches(igurl))
			{
				mesh = ((Geometry)lg.geometry.get(x)).mesh;
				flag = false;
			}
		}
		
		if(flag || mesh == null)
		{
			System.out.println("could not load object");
			return;
		}
		
		if(mesh.triangles.size() == 0)
		{
			System.out.println("object have to be trianglerized");
			return;
		}
		
		//start loading pmesh information from the mesh object
			
		
		tempVerties = mesh.vertices;
		
		int location =0;
		
		//find where the verties are stored and load the data
		while(!((Input)tempVerties.input.get(location)).semantic.matches("POSITION"))
		{
			location++;
		}
		
		String reference = ((Input)tempVerties.input.get(location)).source;
		
		location =  0;
		while(!((Source)mesh.sources.get(location)).id.matches(reference.substring(1)))
		{
			location++;
		}
		
		
		Float_Array fArray = ((Source)mesh.sources.get(location)).floatArray;
		int vCount = ((Source)mesh.sources.get(location)).accessor.count;
		int vStride = ((Source)mesh.sources.get(location)).accessor.stride;
		
		vertArray = new ArrayList<VertCell>();
		numVerts = vCount;
		int place =0;
		
		for(int x =0; x < vCount; x++)
		{
			tempVertCell = new VertCell();
			
			tempVertCell.worldPos.x = fArray.getValue(place);
			tempVertCell.worldPos.y = fArray.getValue(place+1);
			tempVertCell.worldPos.z = fArray.getValue(place+2);
			
			tempVertCell.polys = null;
			vertArray.add(x, tempVertCell);
			
			place += vStride;
			
		}
		
		
		//all of the VertCells are loaded 
		
		
		numSurf = mesh.triangles.size();
		
		tempSurfCell = new SurfCell("tempCollada");
		surfHead = tempSurfCell;
		
		for(int x=0; x < numSurf; x++)
		{
			
			tempTri = (Triangles)mesh.triangles.get(x);
			tempSurfCell.numPoly = tempTri.count;
			tempSurfCell.material = getMaterial(tempTri.material,instanceGeom,collada.material);
			
			tempPolyCell = new PolyCell();
			tempPolyCell.numVerts = tempTri.count;
			tempPolyCell.parentSurf = tempSurfCell;
			tempSurfCell.polyHead = tempPolyCell;
			
			cur = 0;
			for( int z =0 ;z< tempTri.count;z++)
			{
				
				for(int w=0;w<tempTri.inputs.size();w++)
				{
					
					offset = 0;
					
					if(((Input)tempTri.inputs.get(w)).semantic.matches("VERTEX"))
					{
						vertexO = (Input)tempTri.inputs.get(w);
						if(vertexO.offset > offset)
							offset = vertexO.offset;
					}	
					else if(((Input)tempTri.inputs.get(w)).semantic.matches("NORMAL"))
					{
						normalO = (Input)tempTri.inputs.get(w);
						if(normalO.offset > offset)
							offset = normalO.offset;
					}
					else if(((Input)tempTri.inputs.get(w)).semantic.matches("TEXCOORD"))
					{
						textCoordO = (Input)tempTri.inputs.get(w);
						if(textCoordO.offset > offset)
							offset = textCoordO.offset;
					}
					else if(((Input)tempTri.inputs.get(w)).semantic.matches("TEXTANGENT"))
					{
						textTangO = (Input)tempTri.inputs.get(w);
						if(textTangO.offset > offset)
							offset = textTangO.offset;
					}
					
					else if(((Input)tempTri.inputs.get(w)).semantic.matches("TEXBINORMAL"))
					{
						textBinO = (Input)tempTri.inputs.get(w);
						if(textBinO.offset > offset)
							offset = textBinO.offset;
					}
				}
				
				tempPolyCell.numVerts = 3;
								
				int[] vertexLocations = breakInputs(vertexO,tempTri.getLocations(),offset,tempTri.count);
				//int[] normalLocations = breakInputs(normalO,tempTri.getLocations(),offset,tempTri.count);
				
				//Double3D[] norms = this.getValues(normalLocations,normalO.source.substring(1),mesh.sources);		
				
				tempVertCell = new VertCell();
				
				
				tempVertListCell = new VertListCell();
				//tempSurfCell.polyHead = tempPolyCell;
				tempPolyCell.vert = tempVertListCell;
								
				
				
				for(int t = 0 ;t< tempPolyCell.numVerts; t++)
				{
					tempVertListCell.vert = vertexLocations[cur];
					//normals.add(norms[cur]);
					//tempVertListCell.norm = normals.size()-1;
					cur++;
					
					tempPolyListCell = new PolyListCell();
					tempPolyListCell.poly = tempPolyCell;
					
					addToVertArrayPoly(tempVertListCell.vert,tempPolyListCell);
					
					if((t+1) < tempPolyCell.numVerts)
					{
						tempVertListCell.next = new VertListCell();
						tempVertListCell = tempVertListCell.next;
					}
				}
				
				tempVertListCell.next = null;
								
				//int[] textCoordLocations = breakInputs(textCoordO,tempTri.getLocations(),offset,tempTri.count);
				//int[] textTangLocations = breakInputs(textTangO,tempTri.getLocations(),offset,tempTri.count);
				//int[] textBinLocations = breakInputs(textBinO,tempTri.getLocations(),offset,tempTri.count);
				
				if((z+1)< tempTri.count)
				{
					tempPolyCell.next = new PolyCell();
					tempPolyCell = tempPolyCell.next;
					tempPolyCell.parentSurf = tempSurfCell;
				}
				
			}//for # triangles count
			
			tempPolyCell.next = null;
			
			tempSurfCell.next = new SurfCell("tempCollada2");
			tempSurfCell = tempSurfCell.next;
						
		}//for surf
		
		this.calcPolyNorms();
		this.calcVertNorms();
		this.calcBoundingSphere();
		
	}
	
	public int getMaterial(String name,Instance_Geometry instanceGeom,Library_Materials lMaterials)
	{
		
		ArrayList bindings = instanceGeom.instance_material;
		String target=null;
		String effect;
		
		
		for(int x=0;x<bindings.size();x++)
		{
			if(((Instance_Material)bindings.get(x)).symbol.matches(name))
			{
				target = ((Instance_Material)bindings.get(x)).target.substring(1);
				break;
			}
		}
		
		effect = lMaterials.getEffect(target);
		
		for(int x=0;x<materials.length;x++)
		{
			if(materials[x].materialName.matches(effect.substring(1)))
				return x;
			
		}
		
		return 0;
	}
	
	public Double3D[] convertToDouble3DArray(ArrayList list)
	{
		Double3D[] array = new Double3D[list.size()];
		
		for(int x=0;x<array.length;x++)
		{
			array[x] = (Double3D)list.get(x);
		}
		
		return array;
	}
	
	
	public void addToVertArrayPoly(int location,PolyListCell cell)
	{
		VertCell vc = (VertCell)vertArray.get(location);
		PolyListCell cur;
		
		if(vc.polys == null)
			vc.polys = cell;
		
		else
		{
			cur = vc.polys;
			
			while(cur.next != null)
			{
				cur = vc.polys.next;
			}
			
			cur.next = cell;
			cur.next = null;
		}
	}
	
	public void printTest()
	{
					
		SurfCell curSurf = surfHead;
		PolyCell curPoly;
		VertListCell curVert;
		
		while(curSurf != null)
		{
			curPoly = curSurf.polyHead;
			
			while(curPoly != null)
			{
				curVert = curPoly.vert;
				
				while(curVert != null)
				{
					System.out.print(curVert.vert + " ");
					curVert = curVert.next;
				}
				
				System.out.println();
				curPoly = curPoly.next;
			}
			
			curSurf = curSurf.next;
		}
	}
	
	public Double3D[] getValues(int[] locations,String source,ArrayList sources)
	{
		
		boolean flag = true;
		Double3D[] out = new Double3D[locations.length];
		Source tempSource=null;
		Float_Array tempArray;
		int stride;
		
		for(int x=0;x<sources.size() && flag;x++)
		{
			tempSource = (Source)sources.get(x);
			
			if(tempSource.id.matches(source))
				flag = false;
		}
		
		tempArray = tempSource.floatArray;
		stride = tempSource.accessor.stride;
		
		for(int x=0; x<out.length;x++)
		{
			out[x] = new Double3D();
			out[x].x = tempArray.getValue(locations[x]*stride);
			out[x].y = tempArray.getValue(locations[x]*stride+1);
			out[x].z = tempArray.getValue(locations[x]*stride+2);
			
		}
		
		return out;
	}
	
	public int[] breakInputs(Input input, int[] locations, int offset, int count)
	{
		int c = (locations.length/(offset+1));
		int[] out = new int[c];
		int next = input.offset;
		
		for(int x=0;x<c;x++)
		{
			out[x] = locations[next];
			next+=offset+1;
		}
		
		return out;
	}
	
	public void loadEffects(Library_Effects effects)
	{
		Effect tempEffect;
		MaterialCell tempMat;
		
		numMats=effects.effects.size();
		materials = new MaterialCell[numMats];
		
		for(int x=0; x<numMats;x++)
		{
			tempEffect = (Effect)effects.effects.get(x);
			tempMat = new MaterialCell();
			
			tempMat.materialName = tempEffect.id;
			tempMat.ka = new DoubleColor(tempEffect.ambient[0], tempEffect.ambient[1], tempEffect.ambient[2], 1.0);
			tempMat.kd = new DoubleColor(tempEffect.diffuse[0], tempEffect.diffuse[1], tempEffect.diffuse[2], 1.0);
			tempMat.ks =  new DoubleColor(tempEffect.specular[0], tempEffect.specular[1], tempEffect.specular[2], 1.0);
			tempMat.emmColor = new DoubleColor(tempEffect.emission[0], tempEffect.emission[1], tempEffect.emission[2], 1.0);
			tempMat.refractivity.r = tempEffect.transparency;
			tempMat.shiny = tempEffect.shininess;
			tempMat.lineColor = new DoubleColor(tempEffect.diffuse[0], tempEffect.diffuse[1], tempEffect.diffuse[2], 1.0);
			tempMat.doubleSided = false;
						
			this.materials[x]=tempMat;
			
		}
	}
}
