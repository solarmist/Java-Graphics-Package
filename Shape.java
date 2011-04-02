/*
 * This is the generic shape interface class that all shapes implement
 *  
 * @author Joshua Olson
 * @version	1-Apr-2011
 */

interface Shape
{
     public boolean hit(Ray r, double tmin, double tmax, double time, HitRecord record);
     public boolean shadowHit(Ray r, double tmin, double tmax, double time, HitRecord record);
}