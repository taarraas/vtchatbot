/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vt.linguistics.distances;

/**
 *
 * @author taras
 */

public class TextPairs {
        public static final double minimumCompareLevel=0.5,
            similarCompareLevel=0.9;    
    private class Assignment {
        double d[][];
        boolean  f[][];
        int st[], from[];
        int n, m;
        int k;
        public int pairsFrom[], pairsTo[];
        public int pairsCount;
        public void getPairs(double a[][]) {
            d=a;
            n=d.length;
            m=d[0].length;
            k=n+m+2;
            f=new boolean[k][k];
            pairsFrom=new int[Math.min(m,n)];
            pairsTo=new int[Math.min(m,n)];
            pairsCount=0;
            st=new int[k];
            from=new int[k];
            for (int i = 0; i < n; i++) {
                double mx=Double.MAX_VALUE;
                for (int j = 0; j < m; j++) mx = Math.min(mx, d[i][j]);
                for (int j = 0; j < m; j++) d[i][j]-=mx;                
            }
            for (int i = 0; i < m; i++) {
                double mx=Double.MAX_VALUE;
                for (int j = 0; j < n; j++) mx = Math.min(mx, d[j][i]);
                for (int j = 0; j < n; j++) d[j][i]-=mx;                
            }
            
            while (true) {
                                               
                for (int i = 0; i < k; i++) java.util.Arrays.fill(f[i], false);                    
                for (int i = 0; i < n; i++) 
                    for (int j = 0; j < m; j++) 
                        if (d[i][j]<1e-2) f[i][j+n]=true;
                int mm;
                int s=n+m, 
                    t=n+m+1;
                for (int i = 0; i < n; i++) f[s][i]=true;
                for (int i = 0; i < m; i++) f[i+n][t]=true;
                for (mm=0;; mm++) {
                           int sz=1;
                       st[0]=s;
                       java.util.Arrays.fill(from, -1);
                       from[s]=0;
                       for (int cr=0; cr<sz; cr++) {
                           for (int i = 0; i < k; i++) {
                               if(f[st[cr]][i] && from[i]==-1) {
                                   from[i]=st[cr];
                                   st[sz++] = i;
                                   if (i==t) break;
                               }                              
                           }                           
                       }
                       if (from[t]==-1) break;
                       int g=t;
                       while (g!=s) {
                           f[g][from[g]] = true;
                           f[from[g]][g] = false;
                           g=from[g];
                       }
                }
                if (mm==Math.min(m, n)) break;
                //substracting
                double mi=Double.MAX_VALUE;
                for (int i = 0; i < n; i++) 
                    if (from[i] != -1)
                        for (int j = 0; j < m; j++) 
                            if (from[j+n]==-1)
                                mi=Math.min(mi, d[i][j]);
                        
                for (int i = 0; i < n; i++) {
                    if (from[i] != -1) {
                        for (int j = 0; j < m; j++) {
                            d[i][j]-=mi;                            
                        }
                    }                    
                }
                for (int j = 0; j < m; j++) {
                    if (from[j+n]!= -1 ) {
                        for (int i = 0; i < n; i++) {
                            d[i][j]+=mi;
                            
                        }
                    }                    
                }
                
            }
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < m; j++) {
                    if (f[n+j][i]) {
                        pairsFrom[pairsCount]=i;
                        pairsTo[pairsCount]=j;
                        pairsCount++;
                    }
                }                
            }
            
        }
    }
    private double d[][];
    private Assignment assignment=new Assignment();
    private WordDistances wordDistances=new WordDistances();
    public double compare(String a, String b) {
        String aa[]=a.split("[^а-яА-Яa-zA-Z]+");
        String bb[]=b.split("[^а-яА-Яa-zA-Z]+");//(\\#|\\;|\\&|\\s|,|\\.|!|\\?)+
        if (aa.length==0) {
            if (a.equals(b)) return 1;
            else return 0;
        }
        if (bb.length==0) {
            return 0;
        }
        d=new double[aa.length][bb.length];
        double dmax[][]=new double[aa.length][bb.length];
        for (int i = 0; i < d.length; i++) {
            java.util.Arrays.fill(d[i], 0);
            
        }
        for (int i = 0; i < aa.length; i++) {
            String string = aa[i];
            for (int j = 0; j < bb.length; j++) {
                String string1 = bb[j];                
                d[i][j]=1-wordDistances.getDistance(string, string1);
                dmax[i][j]=wordDistances.lastMax;
            }
        }        
        assignment.getPairs(d);
        double res=0, maxres1=0, maxres2=0, maxresc=0;
        boolean a1[]=new boolean[aa.length],
                b1[]=new boolean[bb.length];
        for (int i = 0; i < assignment.pairsCount; i++) {
            int nfr=assignment.pairsFrom[i],
                    nto=assignment.pairsTo[i];
            maxresc+=dmax[nfr][nto];
            a1[nfr]=true;
            b1[nto]=true;
            String str1=aa[nfr],
                    str2=bb[nto];
            if (str1.isEmpty() || str2.isEmpty()) continue;
            if (str1.isEmpty()) continue;
            res+=wordDistances.getDistance(str1, str2);
        }
        for (int i = 0; i < a1.length; i++) {
            if (!a1[i]) maxres1+=wordDistances.getMinMaxDistance(aa[i]);
        }
        for (int i = 0; i < b1.length; i++) {
            if (!b1[i]) maxres2+=wordDistances.getMinMaxDistance(bb[i]);       
        }
        return res/(Math.max(maxres1, maxres2)+maxresc);
    }
    public double comparePairs(double nextToLastRevalent, double lastRevalent) {
        return (nextToLastRevalent*0.7+1)*lastRevalent;
    }
    public double comparePairs(String nextToLastDB, String nextToLast, String lastDB, String last) {
        return comparePairs(compare(nextToLastDB, nextToLast), compare(lastDB, last));
    }    
    
    public static void main(String argv[]) {
        TextPairs tp=new TextPairs();
        String test[]={
            "привет оО",
            "привет оО\nты кто?оО"};
        for (int i = 0; i < test.length; i++) {
            for (int j = i; j < test.length; j++) {
                System.out.println(test[i]+":-=vs=-:"+test[j]+":== "+tp.compare(test[i], test[j]));
            }
        }
    }   
}
    
