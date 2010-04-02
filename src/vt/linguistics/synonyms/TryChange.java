/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vt.linguistics.synonyms;

import vt.linguistics.rml.Lematizer;

/**
 *
 * @author taras
 */
public class TryChange {
    Lematizer lematizer;
    public TryChange(Lematizer lem) {
        lematizer=lem;
    }
    public String change(String form, String word) {
        String anc[]=lematizer.getAllAncodes(form);
        for (int i = 0; i < anc.length; i++) {
            String string = anc[i];
            String ncur=lematizer.toAncode(word, string);
            if (!ncur.isEmpty()) {
                return ncur;
            }
        }
        return "";
    }
    public static void main(String ar[]) {
        TryChange tryChange=new TryChange(Lematizer.getEnglishLematizer());
        System.out.println(tryChange.change("going", "do"));
    }
}
