/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vt.linguistics.distances;

/**
 *
 * @author taras
 */
public interface Distance {
    public double getDistance(String a, String b);
    public double getMaxDistance(String a, String b);
}
