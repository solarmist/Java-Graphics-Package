/**
 * This static class if for sampling techniques.
 */
import java.util.Random;

public class Sample {
	
	static Random Rand = new Random();
	
	//2D sampling
	static void random(Double3D[] samples, int numSamples)
	{
		for (int i = 0; i < numSamples; i++) 
		{
	        samples[i].x = Rand.nextDouble(); //Rand.nextDouble();
	        samples[i].y = Rand.nextDouble();
	    }
	}

	//Jitter assumes numSamples is a perfect square
	static void jitter(Double3D[] samples, int numSamples)
	{
	    int sqrtSamples = (int)Math.sqrt(numSamples);
	    for (int i = 0; i < sqrtSamples; i++) 
	        for (int j = 0; j < sqrtSamples; j++) 
	        {
	        	double x = (i + Rand.nextDouble()) / sqrtSamples;
	        	double y = (j + Rand.nextDouble()) / sqrtSamples;
	            samples[i * sqrtSamples + j].x = x;
	            samples[i * sqrtSamples + j].y = y;
	        }

	}
	
	static void nrooks(Double3D[] samples, int numSamples)
	{
		for (int i = 0; i < numSamples; i++) 
		{
	        samples[i].x = (i + Rand.nextDouble()) / numSamples;
	        samples[i].y = (i + Rand.nextDouble()) / numSamples;
	    }
		
	    //Shuffle the x coords
	    for (int i = numSamples - 2; i >= 0; i--) 
	    {
	        int target = (int) (Rand.nextDouble() * i);
	        double temp = samples[i + 1].x;
	        samples[i + 1].x = samples[target].x;
	        samples[target].x = temp;
	    }
	}
	    
	//Multi-jitter assumes numSamples is a perfect square
	static void multiJitter(Double3D[] samples, int numSamples)
	{
		int sqrtSamples = (int)Math.sqrt(numSamples);
		double subcellWidth = 1.0f / numSamples;
		    
		    //Initialize points to the "canonical" multi-jittered pattern
		    for (int i = 0; i < sqrtSamples; i++) 
		        for (int j = 0; j < sqrtSamples; j++) 
		        {
		            samples[i * sqrtSamples + j].x = 
		                i * sqrtSamples * subcellWidth +
		                j * subcellWidth + Rand.nextDouble() * subcellWidth;
		            samples[i * sqrtSamples + j].y = 
		                j * sqrtSamples * subcellWidth +
		                i * subcellWidth + Rand.nextDouble() * subcellWidth;
		        }
		    
		    //Shuffle x coords within each column and y coords within each row
		    for (int i = 0; i < sqrtSamples; i++) 
		        for (int j = 0; j < sqrtSamples; j++) 
		        {
		            int k = j + (int)(Rand.nextDouble() * (sqrtSamples - j - 1));
		            double t = samples[i * sqrtSamples + j].x;
		            samples[i * sqrtSamples + j].x = samples[i * sqrtSamples + k].x;
		            samples[i * sqrtSamples + k].x = t;
		            
		            k = j + (int)(Rand.nextDouble() * (sqrtSamples - j - 1));
		            t = samples[j * sqrtSamples + i].y;
		            samples[j * sqrtSamples + i].y = samples[k * sqrtSamples + k].y;
		            samples[k * sqrtSamples + i].y = t;
		        }
	}
	
	static void shuffle(Double3D[] samples, int numSamples)
	{
		  for (int i = numSamples - 2; i < 0; i--){
		        int target = (int)(Rand.nextDouble() * i);
		        Double3D temp = samples[i + 1];
		        samples[i + 1] = samples[target];
		        samples[target] = temp;
		    }

	}

	static void boxFilter(Double3D[] samples, int numSamples)
	{
		for (int i = 0; i < numSamples; i++)
		{
	        samples[i].x = samples[i].x - 0.5f;
	        samples[i].y = samples[i].y - 0.5f;
	    }
	}
	
	static void tentFilter(Double3D[] samples, int numSamples)
	{
		 for (int i = 0; i < numSamples; i++)
		 {
		        double x = samples[i].x;
		        double y = samples[i].y;
		        
		        if(x < 0.5f) 
		            samples[i].x = Math.sqrt(2.0 * x) - 1.0;
		        else
		            samples[i].x = 1.0f - Math.sqrt(2.0 - 2.0 * x);
		        
		        if(y < 0.5f) 
		            samples[i].y = Math.sqrt(2.0 * y) - 1.0;
		        else
		            samples[i].y = 1.0 - Math.sqrt(2.0 - 2.0 * y);
		    }
	}
	
	static void cubicSplineFilter(Double3D[] samples, int numSamples)
	{
		 for (int i = 0; i < numSamples; i++) 
		 {
		        double x = samples[i].x;
		        double y = samples[i].y;
		        
		        samples[i].x = cubicFilter(x);
		        samples[i].y = cubicFilter(y);
		    }
	}

	//1D sampling
	static void random(double[] samples, int numSamples)
	{
		for (int i = 0; i < numSamples; i++)
	        samples[i] = Rand.nextDouble();
	}
	
	static void jitter(double[] samples, int numSamples)
	{
		for (int i = 0; i < numSamples; i++)
			samples[i] = (i + Rand.nextDouble()) / numSamples;
	}
	
	static void shuffle(double[] samples, int numSamples)
	{
		for (int i = numSamples - 2; i >= 0; i--) 
		{
	        int target = (int)Rand.nextDouble() * i;
	        double temp = samples[i + 1];
	        samples[i + 1] = samples[target];
	        samples[target] = temp;
	    }
	}

	//Helper function for cubicSplineFilter
	static double solve(double r)
	 {
	    double u = r;
	    for (int i = 0; i < 5; i++)
	        u = (11.0f * r + u * u * (6.0f + u * (8.0f - 9.0f * u))) /
	                (4.0f + 12.0f * u * (1.0f + u * (1.0f - u)));
	    return u;
	}

	static double cubicFilter(double x)
	 {
	    if(x < 1.0f / 24.0f)
	        return Math.pow(24 * x, 0.25f) - 2.0f;
	    else if(x < 0.5)
	        return solve(24.0f * (x - 1.0f / 24.0f) / 11.0f) - 1.0f;
	    else if(x < 23.0f / 24.0f)
	        return 1.0f - solve(24.0f * (23.0f / 24.0f - x) / 11.0f);
	    else
	        return 2 - Math.pow(24.0f * (1.0f - x), 0.25f);
	}

	
}
