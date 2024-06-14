package helper;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.antlr.runtime.ANTLRFileStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.RuleReturnScope;
import org.antlr.stringtemplate.StringTemplateGroup;
import org.antlr.stringtemplate.language.AngleBracketTemplateLexer;

public class RepresentationToIntermediate {
	
	public static String CMinusToJava(String inputFileName) throws IOException, RecognitionException{
		return RepresentationToIntermediate.CMinusToAnotherLanguage("Java.stg", inputFileName);
	}
	
	public static String CMinusToPython(String inputFileName) throws IOException, RecognitionException{
		return RepresentationToIntermediate.CMinusToAnotherLanguage("Python.stg", inputFileName);
	}

	public static String CMinusToAnotherLanguage(String templateFileName, String inputFileName) throws IOException, RecognitionException{
		StringTemplateGroup templates = new StringTemplateGroup(new FileReader(templateFileName),AngleBracketTemplateLexer.class);
		CharStream input = new ANTLRFileStream(inputFileName);
		CMinusLexer lexer = new CMinusLexer(input);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		CMinusParser parser = new CMinusParser(tokens);
		parser.setTemplateLib(templates);
		RuleReturnScope r = parser.program();
		return r.getTemplate().toString();
	}
		
	public static Set<String> getPossibleLanguages(){
		Set<String> answer=new HashSet<String>();
		answer.add("C");
		answer.add("Java");
		answer.add("Python");
		return answer;
	}
}
