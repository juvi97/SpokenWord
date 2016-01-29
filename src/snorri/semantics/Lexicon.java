package snorri.semantics;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import snorri.nonterminals.Noun;
import snorri.nonterminals.Prep;
import snorri.nonterminals.Sentence;
import snorri.nonterminals.TransVerb;

public class Lexicon {
	
	private static Map<String, Definition> lexicon;
	
	static {
		
		lexicon = new HashMap<String, Definition>();
		
		lexicon.put("r", new Definition(Prep.class, null)); //to (pos)
		lexicon.put("n", new Definition(Prep.class, null)); //to (ent)
		lexicon.put("m", new Definition(Prep.class, null)); //in/at
		
		lexicon.put("jAm", new Definition(Noun.class, null)); //tree
		
		lexicon.put("mAA", new Definition(TransVerb.class, null)); //see
		lexicon.put("sDm", new Definition(TransVerb.class, null)); //hear
		
	}
	
	public static Definition lookup(String form) {
		if (lexicon.containsKey(form))
			return lexicon.get(form);
		return null;
	}
	
	public static Set<Entry<String, Definition>> getAllTerminals() {
		return lexicon.entrySet();
	}
	
}
