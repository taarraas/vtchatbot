/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vt.linguistics.distances;

/**
 *
 * @author taras
 */
public class WordFormDistance implements Distance{

    private double getAbsDistance(String a, String b) {
        int ac=a.indexOf(' ');
        int bc=b.indexOf(' ');
        assert ac!=-1;
        assert bc!=-1;
        String as1=a.substring(0, ac),
             as2=a.substring(ac+1),
             bs1=b.substring(0, bc),
             bs2=b.substring(bc+1);
        if (as1.equals(bs1)) {
            if (as2.charAt(0)!=bs2.charAt(0)) {
                return 0.5;
            } else {
                if (as2.equals(bs2)) {
                    return 1;
                } else {
                    return 0.75;
                }
            }
        } else {
            return 0;
        }
    }
    public double getDistance(String a, String b) {
        return getAbsDistance(a, b)*getMaxDistance(a, b);
    }
    private double getMax(String s, String word) {
        switch (s.charAt(0)) {
            case 'а':  return 0.6;// привет тут приветик лимит мозг номер смысл
            case 'б': return 0.4;//але АЛА ли ЛИ ХАРД октав Лукашенко Вах Том Пол - имена
            case 'в': return 0.5;//игрушко дока белых сатана
            case 'г': return 0.5; //существительно
            case 'д': return 0.3; //хай - ХАЯ лан- лана гей- ГЕЙЯ ГЕЯ свет СВЕТА ДАЯ ИЗА ОЧИНА - женские имена неправильные
            case 'е': return 0.8; //несклоняющиеся существительные
            case 'и': return 0.5;// нималенький нималейшего БОТЫ МОЗГИ выборы
            case 'й': return 0.3; //прилагательное
            case 'к': return 1; //глагол            
            case 'л': return 0.7; //причастие
            case 'м': return 0.7; //деепричастие
            case 'н': return 0.2;// видимо ВИДЕТЬ следует - вставные слова
            case 'п': return 1;// видимо ВИДЕТЬ согреваемой называемые
            case 'с': return 0.7;// исчерпать вообщеть представлено убеждён                
            case 'ы': return 0.4; // сам этот тот каков то
            case 'ч': return 0.5;// я,ты, вы
            case 'ш': return 0.3;// они
            case 'щ': return 0.9; //кто что
            case 'ю': return 0.4; //числительное              
            case 'э': return 0.3;// много двух числительные
            case 'я': {  //предлог(яв) или наречие(яб)
                if (s.length()<2) {//українці "подмишка" - неизвестные слова
                    //System.out.println(word+" "+s+" "+origa+" "+origb);
                    return 0.6;
                }
                if(s.charAt(1)=='б') return 0.7;
                else return 0.1;
            } 
            case 'Г': return 0.4;// лучшем-ХОРОШИЙ ярчайшая полнейшем
            case 'Й': { // MAMA
                //System.out.println(word+" "+s+" "+origa+" "+origb);
                    return 0.5; //делов
            }
            case 'Р': return 1; //мужское отчество
            case 'Т': return 1; //мужское имя
            case 'Э': return 0.5;// толку толк Свету народу см-СМОТРЕТЬ виду чаю им пару морозу
            case 'Я': {
                //System.out.println(word+" "+s+" "+origa+" "+origb);
                    return 0.5; //делов мам
            }
            default: {
                throw new RuntimeException("word:"+word+" gram "+s+" was not defined as grammar part ");
            }
        }
    }
    public double getMaxDistance(String a) {
        int ac=a.indexOf(' ');
        if (ac==-1) {
            System.out.println(a);
        }
        assert ac!=-1;
        String as1=a.substring(0, ac),
             as2=a.substring(ac+1);
        return getMax(as2, as1);
    }
    public double getMaxDistance(String a, String b) {
        return Math.max(getMaxDistance(a), getMaxDistance(b));
    }
    
}
