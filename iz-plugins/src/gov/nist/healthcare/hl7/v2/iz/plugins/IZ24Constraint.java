package gov.nist.healthcare.hl7.v2.iz.plugins;

import java.util.ArrayList;
import java.util.Hashtable;

import scala.collection.immutable.List;
import hl7.v2.instance.Element;
import hl7.v2.instance.Query;
import hl7.v2.instance.Simple;

/**
 * This is dummy plugin that can be called within a constraint
 */
public class IZ24Constraint {
	
	/**
	 * @return True if the asssertion succeed, false otherwise
	 */
	public enum OBXT {
		barCoded, presentDate, vaccType, versDate, other
	}

	public boolean assertion(Element context) {
		
		Hashtable<String,ArrayList<OBXT>> ht = new Hashtable<String,ArrayList<OBXT>>();
		
		// All OBXs
		List<Element> OBXList1 = Query.query(context, "5[*].1[1]").get();
		if (OBXList1 == null || OBXList1.size() < 2) {
            return false;
        }

		for(int i = 0; i < OBXList1.size(); i++){
			Element OBX1 = OBXList1.apply(i);
			List<Simple> elm = Query.queryAsSimple(OBX1, "3[1].1[1]").get();
			if(elm != null && elm.size() > 0){
				OBXT t;
				if(elm.apply(0).value().raw().equals("69764-9")){
					t = OBXT.barCoded;
				}
				else if(elm.apply(0).value().raw().equals("29769-7")){
					t = OBXT.presentDate;
				}
				else if(elm.apply(0).value().raw().equals("30956-7")){
					t = OBXT.vaccType;
				}
				else if(elm.apply(0).value().raw().equals("29768-9")){
					t = OBXT.versDate;
				}
				else
					t = OBXT.other;
				
				List<Simple> elm1 = Query.queryAsSimple(OBX1, "4[1]").get();
				if(elm1 != null && elm1.size() > 0){
					String id = elm1.apply(0).value().raw();
					if(ht.containsKey(id)){
						ht.get(id).add(t);
					}
					else {
						ArrayList<OBXT> tmp = new ArrayList<OBXT>();
						tmp.add(t);
						ht.put(id, tmp);
					}
				}

			}
		}
		
		for(String id : ht.keySet()){
			ArrayList<OBXT> tmp = ht.get(id);
			if(tmp.contains(OBXT.barCoded) && tmp.contains(OBXT.presentDate) && tmp.size() == 2)
				continue;
			else if(tmp.contains(OBXT.versDate) && tmp.contains(OBXT.presentDate) && tmp.contains(OBXT.vaccType) && tmp.size() == 3)
				continue;
			else if(!tmp.contains(OBXT.versDate) && !tmp.contains(OBXT.presentDate) && !tmp.contains(OBXT.vaccType) && !tmp.contains(OBXT.barCoded))
				continue;
			else
				return false;
		}
		return true;

	}

}
