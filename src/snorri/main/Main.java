package snorri.main;

import java.util.Arrays;

import snorri.nonterminals.Sentence;
import snorri.parser.Grammar;
import snorri.parser.Node;
import snorri.parser.NonTerminal;
import snorri.parser.Rule;

@SuppressWarnings("unused")
public class Main {

	public static void main(String[] args) {
		
		NonTerminal result = Grammar.parseString("sDm jAm");
		System.out.println("Parse found: " + result);
		
	}

}
