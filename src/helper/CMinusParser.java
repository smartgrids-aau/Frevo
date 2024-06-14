package helper;

// $ANTLR 3.5.2 CMinus.g 2019-04-16 15:49:05

import org.antlr.stringtemplate.*;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import org.antlr.stringtemplate.*;
import org.antlr.stringtemplate.language.*;
import java.util.HashMap;
@SuppressWarnings("all")
public class CMinusParser extends Parser {
	public static final String[] tokenNames = new String[] {
		"<invalid>", "<EOR>", "<DOWN>", "<UP>", "FP", "ID", "INT", "WS", "'!='", 
		"'&&'", "'&'", "'('", "')'", "'*'", "'*='", "'+'", "'++'", "'+='", "','", 
		"'-'", "'--'", "'-='", "'/'", "'/='", "';'", "'<'", "'<<'", "'<='", "'='", 
		"'=='", "'>'", "'>='", "'>>'", "'F'", "'L'", "'LL'", "'LLU'", "'LLu'", 
		"'LU'", "'Lu'", "'U'", "'UL'", "'ULL'", "'Ul'", "'Ull'", "'['", "'[]'", 
		"'[][]'", "']'", "'^'", "'abs'", "'ceil'", "'char'", "'cos'", "'cosh'", 
		"'double'", "'else'", "'exp'", "'f'", "'false'", "'float'", "'floor'", 
		"'for'", "'if'", "'int'", "'l'", "'lU'", "'ll'", "'llU'", "'llu'", "'log'", 
		"'log10'", "'long'", "'lu'", "'pow'", "'return'", "'round'", "'sin'", 
		"'sinh'", "'sqrt'", "'tan'", "'tanh'", "'true'", "'u'", "'uL'", "'uLL'", 
		"'ul'", "'ull'", "'unsigned'", "'{'", "'|'", "'||'", "'}'"
	};
	public static final int EOF=-1;
	public static final int T__8=8;
	public static final int T__9=9;
	public static final int T__10=10;
	public static final int T__11=11;
	public static final int T__12=12;
	public static final int T__13=13;
	public static final int T__14=14;
	public static final int T__15=15;
	public static final int T__16=16;
	public static final int T__17=17;
	public static final int T__18=18;
	public static final int T__19=19;
	public static final int T__20=20;
	public static final int T__21=21;
	public static final int T__22=22;
	public static final int T__23=23;
	public static final int T__24=24;
	public static final int T__25=25;
	public static final int T__26=26;
	public static final int T__27=27;
	public static final int T__28=28;
	public static final int T__29=29;
	public static final int T__30=30;
	public static final int T__31=31;
	public static final int T__32=32;
	public static final int T__33=33;
	public static final int T__34=34;
	public static final int T__35=35;
	public static final int T__36=36;
	public static final int T__37=37;
	public static final int T__38=38;
	public static final int T__39=39;
	public static final int T__40=40;
	public static final int T__41=41;
	public static final int T__42=42;
	public static final int T__43=43;
	public static final int T__44=44;
	public static final int T__45=45;
	public static final int T__46=46;
	public static final int T__47=47;
	public static final int T__48=48;
	public static final int T__49=49;
	public static final int T__50=50;
	public static final int T__51=51;
	public static final int T__52=52;
	public static final int T__53=53;
	public static final int T__54=54;
	public static final int T__55=55;
	public static final int T__56=56;
	public static final int T__57=57;
	public static final int T__58=58;
	public static final int T__59=59;
	public static final int T__60=60;
	public static final int T__61=61;
	public static final int T__62=62;
	public static final int T__63=63;
	public static final int T__64=64;
	public static final int T__65=65;
	public static final int T__66=66;
	public static final int T__67=67;
	public static final int T__68=68;
	public static final int T__69=69;
	public static final int T__70=70;
	public static final int T__71=71;
	public static final int T__72=72;
	public static final int T__73=73;
	public static final int T__74=74;
	public static final int T__75=75;
	public static final int T__76=76;
	public static final int T__77=77;
	public static final int T__78=78;
	public static final int T__79=79;
	public static final int T__80=80;
	public static final int T__81=81;
	public static final int T__82=82;
	public static final int T__83=83;
	public static final int T__84=84;
	public static final int T__85=85;
	public static final int T__86=86;
	public static final int T__87=87;
	public static final int T__88=88;
	public static final int T__89=89;
	public static final int T__90=90;
	public static final int T__91=91;
	public static final int T__92=92;
	public static final int FP=4;
	public static final int ID=5;
	public static final int INT=6;
	public static final int WS=7;

	// delegates
	public Parser[] getDelegates() {
		return new Parser[] {};
	}

	// delegators

	protected static class slist_scope {
		List locals;
		List stats;
	}
	protected Stack<slist_scope> slist_stack = new Stack<slist_scope>();


	public CMinusParser(TokenStream input) {
		this(input, new RecognizerSharedState());
	}
	public CMinusParser(TokenStream input, RecognizerSharedState state) {
		super(input, state);
	}

	protected StringTemplateGroup templateLib =
	  new StringTemplateGroup("CMinusParserTemplates", AngleBracketTemplateLexer.class);

	public void setTemplateLib(StringTemplateGroup templateLib) {
	  this.templateLib = templateLib;
	}
	public StringTemplateGroup getTemplateLib() {
	  return templateLib;
	}
	/** allows convenient multi-value initialization:
	 *  "new STAttrMap().put(...).put(...)"
	 */
	@SuppressWarnings("serial")
	public static class STAttrMap extends HashMap<String, Object> {
		public STAttrMap put(String attrName, Object value) {
			super.put(attrName, value);
			return this;
		}
	}
	@Override public String[] getTokenNames() { return CMinusParser.tokenNames; }
	@Override public String getGrammarFileName() { return "CMinus.g"; }


	protected static class program_scope {
		List globals;
		List functions;
	}
	protected Stack<program_scope> program_stack = new Stack<program_scope>();

	public static class program_return extends ParserRuleReturnScope {
		public StringTemplate st;
		public Object getTemplate() { return st; }
		public String toString() { return st==null?null:st.toString(); }
	};


	// $ANTLR start "program"
	// CMinus.g:23:1: program : ( declaration )+ -> program(globals=$program::globalsfunctions=$program::functions);
	public final CMinusParser.program_return program() throws RecognitionException {
		program_stack.push(new program_scope());
		CMinusParser.program_return retval = new CMinusParser.program_return();
		retval.start = input.LT(1);


		  program_stack.peek().globals = new ArrayList();
		  program_stack.peek().functions = new ArrayList();

		try {
			// CMinus.g:32:5: ( ( declaration )+ -> program(globals=$program::globalsfunctions=$program::functions))
			// CMinus.g:32:9: ( declaration )+
			{
			// CMinus.g:32:9: ( declaration )+
			int cnt1=0;
			loop1:
			while (true) {
				int alt1=2;
				int LA1_0 = input.LA(1);
				if ( (LA1_0==ID||LA1_0==52||LA1_0==55||LA1_0==60||LA1_0==64||LA1_0==72||LA1_0==88) ) {
					alt1=1;
				}

				switch (alt1) {
				case 1 :
					// CMinus.g:32:9: declaration
					{
					pushFollow(FOLLOW_declaration_in_program61);
					declaration();
					state._fsp--;
					if (state.failed) return retval;
					}
					break;

				default :
					if ( cnt1 >= 1 ) break loop1;
					if (state.backtracking>0) {state.failed=true; return retval;}
					EarlyExitException eee = new EarlyExitException(1, input);
					throw eee;
				}
				cnt1++;
			}

			// TEMPLATE REWRITE
			if ( state.backtracking==0 ) {
			  // 33:9: -> program(globals=$program::globalsfunctions=$program::functions)
			  {
			  	retval.st = templateLib.getInstanceOf("program",new STAttrMap().put("globals", program_stack.peek().globals).put("functions", program_stack.peek().functions));
			  }


			}

			}

			retval.stop = input.LT(-1);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			program_stack.pop();
		}
		return retval;
	}
	// $ANTLR end "program"


	public static class declaration_return extends ParserRuleReturnScope {
		public StringTemplate st;
		public Object getTemplate() { return st; }
		public String toString() { return st==null?null:st.toString(); }
	};


	// $ANTLR start "declaration"
	// CMinus.g:36:1: declaration : ( variable |f= function );
	public final CMinusParser.declaration_return declaration() throws RecognitionException {
		CMinusParser.declaration_return retval = new CMinusParser.declaration_return();
		retval.start = input.LT(1);

		ParserRuleReturnScope f =null;
		ParserRuleReturnScope variable1 =null;

		try {
			// CMinus.g:37:5: ( variable |f= function )
			int alt2=2;
			switch ( input.LA(1) ) {
			case 88:
				{
				int LA2_1 = input.LA(2);
				if ( (LA2_1==72) ) {
					int LA2_8 = input.LA(3);
					if ( (LA2_8==72) ) {
						int LA2_12 = input.LA(4);
						if ( (LA2_12==64) ) {
							int LA2_16 = input.LA(5);
							if ( (LA2_16==ID) ) {
								int LA2_11 = input.LA(6);
								if ( (LA2_11==24||LA2_11==28||LA2_11==45) ) {
									alt2=1;
								}
								else if ( (LA2_11==11) ) {
									alt2=2;
								}

								else {
									if (state.backtracking>0) {state.failed=true; return retval;}
									int nvaeMark = input.mark();
									try {
										for (int nvaeConsume = 0; nvaeConsume < 6 - 1; nvaeConsume++) {
											input.consume();
										}
										NoViableAltException nvae =
											new NoViableAltException("", 2, 11, input);
										throw nvae;
									} finally {
										input.rewind(nvaeMark);
									}
								}

							}

							else {
								if (state.backtracking>0) {state.failed=true; return retval;}
								int nvaeMark = input.mark();
								try {
									for (int nvaeConsume = 0; nvaeConsume < 5 - 1; nvaeConsume++) {
										input.consume();
									}
									NoViableAltException nvae =
										new NoViableAltException("", 2, 16, input);
									throw nvae;
								} finally {
									input.rewind(nvaeMark);
								}
							}

						}

						else {
							if (state.backtracking>0) {state.failed=true; return retval;}
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 2, 12, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

					}
					else if ( (LA2_8==ID) ) {
						int LA2_11 = input.LA(4);
						if ( (LA2_11==24||LA2_11==28||LA2_11==45) ) {
							alt2=1;
						}
						else if ( (LA2_11==11) ) {
							alt2=2;
						}

						else {
							if (state.backtracking>0) {state.failed=true; return retval;}
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 2, 11, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

					}

					else {
						if (state.backtracking>0) {state.failed=true; return retval;}
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 2, 8, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}

				}
				else if ( (LA2_1==64) ) {
					int LA2_9 = input.LA(3);
					if ( (LA2_9==ID) ) {
						int LA2_11 = input.LA(4);
						if ( (LA2_11==24||LA2_11==28||LA2_11==45) ) {
							alt2=1;
						}
						else if ( (LA2_11==11) ) {
							alt2=2;
						}

						else {
							if (state.backtracking>0) {state.failed=true; return retval;}
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 2, 11, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

					}

					else {
						if (state.backtracking>0) {state.failed=true; return retval;}
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 2, 9, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}

				}

				else {
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 2, 1, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case 72:
				{
				int LA2_2 = input.LA(2);
				if ( (LA2_2==72) ) {
					int LA2_10 = input.LA(3);
					if ( (LA2_10==64) ) {
						int LA2_13 = input.LA(4);
						if ( (LA2_13==ID) ) {
							int LA2_11 = input.LA(5);
							if ( (LA2_11==24||LA2_11==28||LA2_11==45) ) {
								alt2=1;
							}
							else if ( (LA2_11==11) ) {
								alt2=2;
							}

							else {
								if (state.backtracking>0) {state.failed=true; return retval;}
								int nvaeMark = input.mark();
								try {
									for (int nvaeConsume = 0; nvaeConsume < 5 - 1; nvaeConsume++) {
										input.consume();
									}
									NoViableAltException nvae =
										new NoViableAltException("", 2, 11, input);
									throw nvae;
								} finally {
									input.rewind(nvaeMark);
								}
							}

						}

						else {
							if (state.backtracking>0) {state.failed=true; return retval;}
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 2, 13, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

					}

					else {
						if (state.backtracking>0) {state.failed=true; return retval;}
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 2, 10, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}

				}
				else if ( (LA2_2==ID) ) {
					int LA2_11 = input.LA(3);
					if ( (LA2_11==24||LA2_11==28||LA2_11==45) ) {
						alt2=1;
					}
					else if ( (LA2_11==11) ) {
						alt2=2;
					}

					else {
						if (state.backtracking>0) {state.failed=true; return retval;}
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 2, 11, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}

				}

				else {
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 2, 2, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case 64:
				{
				int LA2_3 = input.LA(2);
				if ( (LA2_3==ID) ) {
					int LA2_11 = input.LA(3);
					if ( (LA2_11==24||LA2_11==28||LA2_11==45) ) {
						alt2=1;
					}
					else if ( (LA2_11==11) ) {
						alt2=2;
					}

					else {
						if (state.backtracking>0) {state.failed=true; return retval;}
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 2, 11, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}

				}

				else {
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 2, 3, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case 52:
				{
				int LA2_4 = input.LA(2);
				if ( (LA2_4==ID) ) {
					int LA2_11 = input.LA(3);
					if ( (LA2_11==24||LA2_11==28||LA2_11==45) ) {
						alt2=1;
					}
					else if ( (LA2_11==11) ) {
						alt2=2;
					}

					else {
						if (state.backtracking>0) {state.failed=true; return retval;}
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 2, 11, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}

				}

				else {
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 2, 4, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case 60:
				{
				int LA2_5 = input.LA(2);
				if ( (LA2_5==ID) ) {
					int LA2_11 = input.LA(3);
					if ( (LA2_11==24||LA2_11==28||LA2_11==45) ) {
						alt2=1;
					}
					else if ( (LA2_11==11) ) {
						alt2=2;
					}

					else {
						if (state.backtracking>0) {state.failed=true; return retval;}
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 2, 11, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}

				}

				else {
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 2, 5, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case 55:
				{
				int LA2_6 = input.LA(2);
				if ( (LA2_6==ID) ) {
					int LA2_11 = input.LA(3);
					if ( (LA2_11==24||LA2_11==28||LA2_11==45) ) {
						alt2=1;
					}
					else if ( (LA2_11==11) ) {
						alt2=2;
					}

					else {
						if (state.backtracking>0) {state.failed=true; return retval;}
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 2, 11, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}

				}

				else {
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 2, 6, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case ID:
				{
				int LA2_7 = input.LA(2);
				if ( (LA2_7==ID) ) {
					int LA2_11 = input.LA(3);
					if ( (LA2_11==24||LA2_11==28||LA2_11==45) ) {
						alt2=1;
					}
					else if ( (LA2_11==11) ) {
						alt2=2;
					}

					else {
						if (state.backtracking>0) {state.failed=true; return retval;}
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 2, 11, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}

				}

				else {
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 2, 7, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			default:
				if (state.backtracking>0) {state.failed=true; return retval;}
				NoViableAltException nvae =
					new NoViableAltException("", 2, 0, input);
				throw nvae;
			}
			switch (alt2) {
				case 1 :
					// CMinus.g:37:9: variable
					{
					pushFollow(FOLLOW_variable_in_declaration102);
					variable1=variable();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) {program_stack.peek().globals.add((variable1!=null?((StringTemplate)variable1.getTemplate()):null));}
					}
					break;
				case 2 :
					// CMinus.g:38:9: f= function
					{
					pushFollow(FOLLOW_function_in_declaration118);
					f=function();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) {program_stack.peek().functions.add((f!=null?((StringTemplate)f.getTemplate()):null));}
					}
					break;

			}
			retval.stop = input.LT(-1);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "declaration"


	public static class variable_return extends ParserRuleReturnScope {
		public StringTemplate st;
		public Object getTemplate() { return st; }
		public String toString() { return st==null?null:st.toString(); }
	};


	// $ANTLR start "variable"
	// CMinus.g:44:1: variable : ( type declarator ';' -> {$function.size()>0 && $function::name==null}? globalVariable(type=$type.stname=$declarator.st) -> variable(type=$type.stname=$declarator.st)| type declarator '=' expr ';' -> {$function.size()>0 && $function::name==null}? globalVariableInit(type=$type.stname=$declarator.stvalue=$expr.st) -> variableInit(type=$type.stname=$declarator.stvalue=$expr.st)| type declarator '[' aexpr ']' ';' -> {$function.size()>0 && $function::name==null}? globalArrayDeclaration(type=$type.stname=$declarator.stsize=$aexpr.st) -> arrayDeclaration(type=$type.stname=$declarator.stsize=$aexpr.st)| type declarator '[' aexpr ']' '=' arrayinit ';' -> {$function.size()>0 && $function::name==null}? globalArrayInitialization(type=$type.stname=$declarator.stvalues=$arrayinit.stsize=$aexpr.st) -> arrayInitialization(type=$type.stname=$declarator.stvalues=$arrayinit.stsize=$aexpr.st)| type declarator '[' a= aexpr ']' '[' b= aexpr ']' ';' -> {$function.size()>0 && $function::name==null}? globalDoubleArrayDeclaration(type=$type.stname=$declarator.stsizeone=$a.stsizetwo=$b.st) -> doubleArrayDeclaration(type=$type.stname=$declarator.stsize=$a.stsizetwo=$b.st)| type declarator '[' a= aexpr ']' '[' b= aexpr ']' '=' arrayinit ';' -> {$function.size()>0 && $function::name==null}? globalDoubleArrayInitialization(type=$type.stname=$declarator.stvalues=$arrayinit.stsizeone=$a.stsizetwo=$b.st) -> doubleArrayInitialization(type=$type.stname=$declarator.stvalues=$arrayinit.stsizeone=$a.stsizetwo=$b.st));
	public final CMinusParser.variable_return variable() throws RecognitionException {
		CMinusParser.variable_return retval = new CMinusParser.variable_return();
		retval.start = input.LT(1);

		ParserRuleReturnScope a =null;
		ParserRuleReturnScope b =null;
		ParserRuleReturnScope type2 =null;
		ParserRuleReturnScope declarator3 =null;
		ParserRuleReturnScope type4 =null;
		ParserRuleReturnScope declarator5 =null;
		ParserRuleReturnScope expr6 =null;
		ParserRuleReturnScope type7 =null;
		ParserRuleReturnScope declarator8 =null;
		ParserRuleReturnScope aexpr9 =null;
		ParserRuleReturnScope type10 =null;
		ParserRuleReturnScope declarator11 =null;
		ParserRuleReturnScope arrayinit12 =null;
		ParserRuleReturnScope aexpr13 =null;
		ParserRuleReturnScope type14 =null;
		ParserRuleReturnScope declarator15 =null;
		ParserRuleReturnScope type16 =null;
		ParserRuleReturnScope declarator17 =null;
		ParserRuleReturnScope arrayinit18 =null;

		try {
			// CMinus.g:45:5: ( type declarator ';' -> {$function.size()>0 && $function::name==null}? globalVariable(type=$type.stname=$declarator.st) -> variable(type=$type.stname=$declarator.st)| type declarator '=' expr ';' -> {$function.size()>0 && $function::name==null}? globalVariableInit(type=$type.stname=$declarator.stvalue=$expr.st) -> variableInit(type=$type.stname=$declarator.stvalue=$expr.st)| type declarator '[' aexpr ']' ';' -> {$function.size()>0 && $function::name==null}? globalArrayDeclaration(type=$type.stname=$declarator.stsize=$aexpr.st) -> arrayDeclaration(type=$type.stname=$declarator.stsize=$aexpr.st)| type declarator '[' aexpr ']' '=' arrayinit ';' -> {$function.size()>0 && $function::name==null}? globalArrayInitialization(type=$type.stname=$declarator.stvalues=$arrayinit.stsize=$aexpr.st) -> arrayInitialization(type=$type.stname=$declarator.stvalues=$arrayinit.stsize=$aexpr.st)| type declarator '[' a= aexpr ']' '[' b= aexpr ']' ';' -> {$function.size()>0 && $function::name==null}? globalDoubleArrayDeclaration(type=$type.stname=$declarator.stsizeone=$a.stsizetwo=$b.st) -> doubleArrayDeclaration(type=$type.stname=$declarator.stsize=$a.stsizetwo=$b.st)| type declarator '[' a= aexpr ']' '[' b= aexpr ']' '=' arrayinit ';' -> {$function.size()>0 && $function::name==null}? globalDoubleArrayInitialization(type=$type.stname=$declarator.stvalues=$arrayinit.stsizeone=$a.stsizetwo=$b.st) -> doubleArrayInitialization(type=$type.stname=$declarator.stvalues=$arrayinit.stsizeone=$a.stsizetwo=$b.st))
			int alt3=6;
			switch ( input.LA(1) ) {
			case 88:
				{
				int LA3_1 = input.LA(2);
				if ( (synpred3_CMinus()) ) {
					alt3=1;
				}
				else if ( (synpred4_CMinus()) ) {
					alt3=2;
				}
				else if ( (synpred5_CMinus()) ) {
					alt3=3;
				}
				else if ( (synpred6_CMinus()) ) {
					alt3=4;
				}
				else if ( (synpred7_CMinus()) ) {
					alt3=5;
				}
				else if ( (true) ) {
					alt3=6;
				}

				}
				break;
			case 72:
				{
				int LA3_2 = input.LA(2);
				if ( (synpred3_CMinus()) ) {
					alt3=1;
				}
				else if ( (synpred4_CMinus()) ) {
					alt3=2;
				}
				else if ( (synpred5_CMinus()) ) {
					alt3=3;
				}
				else if ( (synpred6_CMinus()) ) {
					alt3=4;
				}
				else if ( (synpred7_CMinus()) ) {
					alt3=5;
				}
				else if ( (true) ) {
					alt3=6;
				}

				}
				break;
			case 64:
				{
				int LA3_3 = input.LA(2);
				if ( (synpred3_CMinus()) ) {
					alt3=1;
				}
				else if ( (synpred4_CMinus()) ) {
					alt3=2;
				}
				else if ( (synpred5_CMinus()) ) {
					alt3=3;
				}
				else if ( (synpred6_CMinus()) ) {
					alt3=4;
				}
				else if ( (synpred7_CMinus()) ) {
					alt3=5;
				}
				else if ( (true) ) {
					alt3=6;
				}

				}
				break;
			case 52:
				{
				int LA3_4 = input.LA(2);
				if ( (synpred3_CMinus()) ) {
					alt3=1;
				}
				else if ( (synpred4_CMinus()) ) {
					alt3=2;
				}
				else if ( (synpred5_CMinus()) ) {
					alt3=3;
				}
				else if ( (synpred6_CMinus()) ) {
					alt3=4;
				}
				else if ( (synpred7_CMinus()) ) {
					alt3=5;
				}
				else if ( (true) ) {
					alt3=6;
				}

				}
				break;
			case 60:
				{
				int LA3_5 = input.LA(2);
				if ( (synpred3_CMinus()) ) {
					alt3=1;
				}
				else if ( (synpred4_CMinus()) ) {
					alt3=2;
				}
				else if ( (synpred5_CMinus()) ) {
					alt3=3;
				}
				else if ( (synpred6_CMinus()) ) {
					alt3=4;
				}
				else if ( (synpred7_CMinus()) ) {
					alt3=5;
				}
				else if ( (true) ) {
					alt3=6;
				}

				}
				break;
			case 55:
				{
				int LA3_6 = input.LA(2);
				if ( (synpred3_CMinus()) ) {
					alt3=1;
				}
				else if ( (synpred4_CMinus()) ) {
					alt3=2;
				}
				else if ( (synpred5_CMinus()) ) {
					alt3=3;
				}
				else if ( (synpred6_CMinus()) ) {
					alt3=4;
				}
				else if ( (synpred7_CMinus()) ) {
					alt3=5;
				}
				else if ( (true) ) {
					alt3=6;
				}

				}
				break;
			case ID:
				{
				int LA3_7 = input.LA(2);
				if ( (synpred3_CMinus()) ) {
					alt3=1;
				}
				else if ( (synpred4_CMinus()) ) {
					alt3=2;
				}
				else if ( (synpred5_CMinus()) ) {
					alt3=3;
				}
				else if ( (synpred6_CMinus()) ) {
					alt3=4;
				}
				else if ( (synpred7_CMinus()) ) {
					alt3=5;
				}
				else if ( (true) ) {
					alt3=6;
				}

				}
				break;
			default:
				if (state.backtracking>0) {state.failed=true; return retval;}
				NoViableAltException nvae =
					new NoViableAltException("", 3, 0, input);
				throw nvae;
			}
			switch (alt3) {
				case 1 :
					// CMinus.g:45:9: type declarator ';'
					{
					pushFollow(FOLLOW_type_in_variable142);
					type2=type();
					state._fsp--;
					if (state.failed) return retval;
					pushFollow(FOLLOW_declarator_in_variable144);
					declarator3=declarator();
					state._fsp--;
					if (state.failed) return retval;
					match(input,24,FOLLOW_24_in_variable146); if (state.failed) return retval;
					// TEMPLATE REWRITE
					if ( state.backtracking==0 ) {
					  // 46:9: -> {$function.size()>0 && $function::name==null}? globalVariable(type=$type.stname=$declarator.st)
					  if (function_stack.size()>0 && function_stack.peek().name==null) {
					  	retval.st = templateLib.getInstanceOf("globalVariable",new STAttrMap().put("type", (type2!=null?((StringTemplate)type2.getTemplate()):null)).put("name", (declarator3!=null?((StringTemplate)declarator3.getTemplate()):null)));
					  }

					  else // 48:9: -> variable(type=$type.stname=$declarator.st)
					  {
					  	retval.st = templateLib.getInstanceOf("variable",new STAttrMap().put("type", (type2!=null?((StringTemplate)type2.getTemplate()):null)).put("name", (declarator3!=null?((StringTemplate)declarator3.getTemplate()):null)));
					  }


					}

					}
					break;
				case 2 :
					// CMinus.g:49:6: type declarator '=' expr ';'
					{
					pushFollow(FOLLOW_type_in_variable208);
					type4=type();
					state._fsp--;
					if (state.failed) return retval;
					pushFollow(FOLLOW_declarator_in_variable210);
					declarator5=declarator();
					state._fsp--;
					if (state.failed) return retval;
					match(input,28,FOLLOW_28_in_variable212); if (state.failed) return retval;
					pushFollow(FOLLOW_expr_in_variable214);
					expr6=expr();
					state._fsp--;
					if (state.failed) return retval;
					match(input,24,FOLLOW_24_in_variable216); if (state.failed) return retval;
					// TEMPLATE REWRITE
					if ( state.backtracking==0 ) {
					  // 50:6: -> {$function.size()>0 && $function::name==null}? globalVariableInit(type=$type.stname=$declarator.stvalue=$expr.st)
					  if (function_stack.size()>0 && function_stack.peek().name==null) {
					  	retval.st = templateLib.getInstanceOf("globalVariableInit",new STAttrMap().put("type", (type4!=null?((StringTemplate)type4.getTemplate()):null)).put("name", (declarator5!=null?((StringTemplate)declarator5.getTemplate()):null)).put("value", (expr6!=null?((StringTemplate)expr6.getTemplate()):null)));
					  }

					  else // 52:9: -> variableInit(type=$type.stname=$declarator.stvalue=$expr.st)
					  {
					  	retval.st = templateLib.getInstanceOf("variableInit",new STAttrMap().put("type", (type4!=null?((StringTemplate)type4.getTemplate()):null)).put("name", (declarator5!=null?((StringTemplate)declarator5.getTemplate()):null)).put("value", (expr6!=null?((StringTemplate)expr6.getTemplate()):null)));
					  }


					}

					}
					break;
				case 3 :
					// CMinus.g:53:6: type declarator '[' aexpr ']' ';'
					{
					pushFollow(FOLLOW_type_in_variable283);
					type7=type();
					state._fsp--;
					if (state.failed) return retval;
					pushFollow(FOLLOW_declarator_in_variable285);
					declarator8=declarator();
					state._fsp--;
					if (state.failed) return retval;
					match(input,45,FOLLOW_45_in_variable287); if (state.failed) return retval;
					pushFollow(FOLLOW_aexpr_in_variable289);
					aexpr9=aexpr();
					state._fsp--;
					if (state.failed) return retval;
					match(input,48,FOLLOW_48_in_variable291); if (state.failed) return retval;
					match(input,24,FOLLOW_24_in_variable293); if (state.failed) return retval;
					// TEMPLATE REWRITE
					if ( state.backtracking==0 ) {
					  // 54:6: -> {$function.size()>0 && $function::name==null}? globalArrayDeclaration(type=$type.stname=$declarator.stsize=$aexpr.st)
					  if (function_stack.size()>0 && function_stack.peek().name==null) {
					  	retval.st = templateLib.getInstanceOf("globalArrayDeclaration",new STAttrMap().put("type", (type7!=null?((StringTemplate)type7.getTemplate()):null)).put("name", (declarator8!=null?((StringTemplate)declarator8.getTemplate()):null)).put("size", (aexpr9!=null?((StringTemplate)aexpr9.getTemplate()):null)));
					  }

					  else // 56:9: -> arrayDeclaration(type=$type.stname=$declarator.stsize=$aexpr.st)
					  {
					  	retval.st = templateLib.getInstanceOf("arrayDeclaration",new STAttrMap().put("type", (type7!=null?((StringTemplate)type7.getTemplate()):null)).put("name", (declarator8!=null?((StringTemplate)declarator8.getTemplate()):null)).put("size", (aexpr9!=null?((StringTemplate)aexpr9.getTemplate()):null)));
					  }


					}

					}
					break;
				case 4 :
					// CMinus.g:57:6: type declarator '[' aexpr ']' '=' arrayinit ';'
					{
					pushFollow(FOLLOW_type_in_variable360);
					type10=type();
					state._fsp--;
					if (state.failed) return retval;
					pushFollow(FOLLOW_declarator_in_variable362);
					declarator11=declarator();
					state._fsp--;
					if (state.failed) return retval;
					match(input,45,FOLLOW_45_in_variable364); if (state.failed) return retval;
					pushFollow(FOLLOW_aexpr_in_variable366);
					aexpr13=aexpr();
					state._fsp--;
					if (state.failed) return retval;
					match(input,48,FOLLOW_48_in_variable368); if (state.failed) return retval;
					match(input,28,FOLLOW_28_in_variable370); if (state.failed) return retval;
					pushFollow(FOLLOW_arrayinit_in_variable372);
					arrayinit12=arrayinit();
					state._fsp--;
					if (state.failed) return retval;
					match(input,24,FOLLOW_24_in_variable374); if (state.failed) return retval;
					// TEMPLATE REWRITE
					if ( state.backtracking==0 ) {
					  // 58:6: -> {$function.size()>0 && $function::name==null}? globalArrayInitialization(type=$type.stname=$declarator.stvalues=$arrayinit.stsize=$aexpr.st)
					  if (function_stack.size()>0 && function_stack.peek().name==null) {
					  	retval.st = templateLib.getInstanceOf("globalArrayInitialization",new STAttrMap().put("type", (type10!=null?((StringTemplate)type10.getTemplate()):null)).put("name", (declarator11!=null?((StringTemplate)declarator11.getTemplate()):null)).put("values", (arrayinit12!=null?((StringTemplate)arrayinit12.getTemplate()):null)).put("size", (aexpr13!=null?((StringTemplate)aexpr13.getTemplate()):null)));
					  }

					  else // 60:9: -> arrayInitialization(type=$type.stname=$declarator.stvalues=$arrayinit.stsize=$aexpr.st)
					  {
					  	retval.st = templateLib.getInstanceOf("arrayInitialization",new STAttrMap().put("type", (type10!=null?((StringTemplate)type10.getTemplate()):null)).put("name", (declarator11!=null?((StringTemplate)declarator11.getTemplate()):null)).put("values", (arrayinit12!=null?((StringTemplate)arrayinit12.getTemplate()):null)).put("size", (aexpr13!=null?((StringTemplate)aexpr13.getTemplate()):null)));
					  }


					}

					}
					break;
				case 5 :
					// CMinus.g:61:6: type declarator '[' a= aexpr ']' '[' b= aexpr ']' ';'
					{
					pushFollow(FOLLOW_type_in_variable449);
					type14=type();
					state._fsp--;
					if (state.failed) return retval;
					pushFollow(FOLLOW_declarator_in_variable451);
					declarator15=declarator();
					state._fsp--;
					if (state.failed) return retval;
					match(input,45,FOLLOW_45_in_variable453); if (state.failed) return retval;
					pushFollow(FOLLOW_aexpr_in_variable457);
					a=aexpr();
					state._fsp--;
					if (state.failed) return retval;
					match(input,48,FOLLOW_48_in_variable459); if (state.failed) return retval;
					match(input,45,FOLLOW_45_in_variable461); if (state.failed) return retval;
					pushFollow(FOLLOW_aexpr_in_variable465);
					b=aexpr();
					state._fsp--;
					if (state.failed) return retval;
					match(input,48,FOLLOW_48_in_variable467); if (state.failed) return retval;
					match(input,24,FOLLOW_24_in_variable469); if (state.failed) return retval;
					// TEMPLATE REWRITE
					if ( state.backtracking==0 ) {
					  // 62:6: -> {$function.size()>0 && $function::name==null}? globalDoubleArrayDeclaration(type=$type.stname=$declarator.stsizeone=$a.stsizetwo=$b.st)
					  if (function_stack.size()>0 && function_stack.peek().name==null) {
					  	retval.st = templateLib.getInstanceOf("globalDoubleArrayDeclaration",new STAttrMap().put("type", (type14!=null?((StringTemplate)type14.getTemplate()):null)).put("name", (declarator15!=null?((StringTemplate)declarator15.getTemplate()):null)).put("sizeone", (a!=null?((StringTemplate)a.getTemplate()):null)).put("sizetwo", (b!=null?((StringTemplate)b.getTemplate()):null)));
					  }

					  else // 64:9: -> doubleArrayDeclaration(type=$type.stname=$declarator.stsize=$a.stsizetwo=$b.st)
					  {
					  	retval.st = templateLib.getInstanceOf("doubleArrayDeclaration",new STAttrMap().put("type", (type14!=null?((StringTemplate)type14.getTemplate()):null)).put("name", (declarator15!=null?((StringTemplate)declarator15.getTemplate()):null)).put("size", (a!=null?((StringTemplate)a.getTemplate()):null)).put("sizetwo", (b!=null?((StringTemplate)b.getTemplate()):null)));
					  }


					}

					}
					break;
				case 6 :
					// CMinus.g:65:6: type declarator '[' a= aexpr ']' '[' b= aexpr ']' '=' arrayinit ';'
					{
					pushFollow(FOLLOW_type_in_variable544);
					type16=type();
					state._fsp--;
					if (state.failed) return retval;
					pushFollow(FOLLOW_declarator_in_variable546);
					declarator17=declarator();
					state._fsp--;
					if (state.failed) return retval;
					match(input,45,FOLLOW_45_in_variable548); if (state.failed) return retval;
					pushFollow(FOLLOW_aexpr_in_variable552);
					a=aexpr();
					state._fsp--;
					if (state.failed) return retval;
					match(input,48,FOLLOW_48_in_variable554); if (state.failed) return retval;
					match(input,45,FOLLOW_45_in_variable556); if (state.failed) return retval;
					pushFollow(FOLLOW_aexpr_in_variable560);
					b=aexpr();
					state._fsp--;
					if (state.failed) return retval;
					match(input,48,FOLLOW_48_in_variable562); if (state.failed) return retval;
					match(input,28,FOLLOW_28_in_variable564); if (state.failed) return retval;
					pushFollow(FOLLOW_arrayinit_in_variable566);
					arrayinit18=arrayinit();
					state._fsp--;
					if (state.failed) return retval;
					match(input,24,FOLLOW_24_in_variable568); if (state.failed) return retval;
					// TEMPLATE REWRITE
					if ( state.backtracking==0 ) {
					  // 66:6: -> {$function.size()>0 && $function::name==null}? globalDoubleArrayInitialization(type=$type.stname=$declarator.stvalues=$arrayinit.stsizeone=$a.stsizetwo=$b.st)
					  if (function_stack.size()>0 && function_stack.peek().name==null) {
					  	retval.st = templateLib.getInstanceOf("globalDoubleArrayInitialization",new STAttrMap().put("type", (type16!=null?((StringTemplate)type16.getTemplate()):null)).put("name", (declarator17!=null?((StringTemplate)declarator17.getTemplate()):null)).put("values", (arrayinit18!=null?((StringTemplate)arrayinit18.getTemplate()):null)).put("sizeone", (a!=null?((StringTemplate)a.getTemplate()):null)).put("sizetwo", (b!=null?((StringTemplate)b.getTemplate()):null)));
					  }

					  else // 68:9: -> doubleArrayInitialization(type=$type.stname=$declarator.stvalues=$arrayinit.stsizeone=$a.stsizetwo=$b.st)
					  {
					  	retval.st = templateLib.getInstanceOf("doubleArrayInitialization",new STAttrMap().put("type", (type16!=null?((StringTemplate)type16.getTemplate()):null)).put("name", (declarator17!=null?((StringTemplate)declarator17.getTemplate()):null)).put("values", (arrayinit18!=null?((StringTemplate)arrayinit18.getTemplate()):null)).put("sizeone", (a!=null?((StringTemplate)a.getTemplate()):null)).put("sizetwo", (b!=null?((StringTemplate)b.getTemplate()):null)));
					  }


					}

					}
					break;

			}
			retval.stop = input.LT(-1);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "variable"


	public static class arrayinit_return extends ParserRuleReturnScope {
		public StringTemplate st;
		public Object getTemplate() { return st; }
		public String toString() { return st==null?null:st.toString(); }
	};


	// $ANTLR start "arrayinit"
	// CMinus.g:71:1: arrayinit : ( '{' (p+= realnum ( ',' p+= realnum )* )? '}' -> arrayInitializationBody(args=$p)| '{' (p+= arrayinit ( ',' p+= arrayinit )* )? '}' -> arrayInitializationBody(args=$p));
	public final CMinusParser.arrayinit_return arrayinit() throws RecognitionException {
		CMinusParser.arrayinit_return retval = new CMinusParser.arrayinit_return();
		retval.start = input.LT(1);

		List<Object> list_p=null;
		RuleReturnScope p = null;
		try {
			// CMinus.g:72:5: ( '{' (p+= realnum ( ',' p+= realnum )* )? '}' -> arrayInitializationBody(args=$p)| '{' (p+= arrayinit ( ',' p+= arrayinit )* )? '}' -> arrayInitializationBody(args=$p))
			int alt8=2;
			int LA8_0 = input.LA(1);
			if ( (LA8_0==89) ) {
				switch ( input.LA(2) ) {
				case FP:
				case INT:
				case 19:
					{
					alt8=1;
					}
					break;
				case 92:
					{
					int LA8_3 = input.LA(3);
					if ( (synpred10_CMinus()) ) {
						alt8=1;
					}
					else if ( (true) ) {
						alt8=2;
					}

					}
					break;
				case 89:
					{
					alt8=2;
					}
					break;
				default:
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 8, 1, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}
			}

			else {
				if (state.backtracking>0) {state.failed=true; return retval;}
				NoViableAltException nvae =
					new NoViableAltException("", 8, 0, input);
				throw nvae;
			}

			switch (alt8) {
				case 1 :
					// CMinus.g:72:9: '{' (p+= realnum ( ',' p+= realnum )* )? '}'
					{
					match(input,89,FOLLOW_89_in_arrayinit664); if (state.failed) return retval;
					// CMinus.g:72:13: (p+= realnum ( ',' p+= realnum )* )?
					int alt5=2;
					int LA5_0 = input.LA(1);
					if ( (LA5_0==FP||LA5_0==INT||LA5_0==19) ) {
						alt5=1;
					}
					switch (alt5) {
						case 1 :
							// CMinus.g:72:15: p+= realnum ( ',' p+= realnum )*
							{
							pushFollow(FOLLOW_realnum_in_arrayinit670);
							p=realnum();
							state._fsp--;
							if (state.failed) return retval;
							if (list_p==null) list_p=new ArrayList<Object>();
							list_p.add(p.getTemplate());
							// CMinus.g:72:26: ( ',' p+= realnum )*
							loop4:
							while (true) {
								int alt4=2;
								int LA4_0 = input.LA(1);
								if ( (LA4_0==18) ) {
									alt4=1;
								}

								switch (alt4) {
								case 1 :
									// CMinus.g:72:28: ',' p+= realnum
									{
									match(input,18,FOLLOW_18_in_arrayinit674); if (state.failed) return retval;
									pushFollow(FOLLOW_realnum_in_arrayinit678);
									p=realnum();
									state._fsp--;
									if (state.failed) return retval;
									if (list_p==null) list_p=new ArrayList<Object>();
									list_p.add(p.getTemplate());
									}
									break;

								default :
									break loop4;
								}
							}

							}
							break;

					}

					match(input,92,FOLLOW_92_in_arrayinit686); if (state.failed) return retval;
					// TEMPLATE REWRITE
					if ( state.backtracking==0 ) {
					  // 72:53: -> arrayInitializationBody(args=$p)
					  {
					  	retval.st = templateLib.getInstanceOf("arrayInitializationBody",new STAttrMap().put("args", list_p));
					  }


					}

					}
					break;
				case 2 :
					// CMinus.g:73:6: '{' (p+= arrayinit ( ',' p+= arrayinit )* )? '}'
					{
					match(input,89,FOLLOW_89_in_arrayinit702); if (state.failed) return retval;
					// CMinus.g:73:10: (p+= arrayinit ( ',' p+= arrayinit )* )?
					int alt7=2;
					int LA7_0 = input.LA(1);
					if ( (LA7_0==89) ) {
						alt7=1;
					}
					switch (alt7) {
						case 1 :
							// CMinus.g:73:12: p+= arrayinit ( ',' p+= arrayinit )*
							{
							pushFollow(FOLLOW_arrayinit_in_arrayinit708);
							p=arrayinit();
							state._fsp--;
							if (state.failed) return retval;
							if (list_p==null) list_p=new ArrayList<Object>();
							list_p.add(p.getTemplate());
							// CMinus.g:73:25: ( ',' p+= arrayinit )*
							loop6:
							while (true) {
								int alt6=2;
								int LA6_0 = input.LA(1);
								if ( (LA6_0==18) ) {
									alt6=1;
								}

								switch (alt6) {
								case 1 :
									// CMinus.g:73:27: ',' p+= arrayinit
									{
									match(input,18,FOLLOW_18_in_arrayinit712); if (state.failed) return retval;
									pushFollow(FOLLOW_arrayinit_in_arrayinit716);
									p=arrayinit();
									state._fsp--;
									if (state.failed) return retval;
									if (list_p==null) list_p=new ArrayList<Object>();
									list_p.add(p.getTemplate());
									}
									break;

								default :
									break loop6;
								}
							}

							}
							break;

					}

					match(input,92,FOLLOW_92_in_arrayinit724); if (state.failed) return retval;
					// TEMPLATE REWRITE
					if ( state.backtracking==0 ) {
					  // 73:54: -> arrayInitializationBody(args=$p)
					  {
					  	retval.st = templateLib.getInstanceOf("arrayInitializationBody",new STAttrMap().put("args", list_p));
					  }


					}

					}
					break;

			}
			retval.stop = input.LT(-1);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "arrayinit"


	public static class declarator_return extends ParserRuleReturnScope {
		public StringTemplate st;
		public Object getTemplate() { return st; }
		public String toString() { return st==null?null:st.toString(); }
	};


	// $ANTLR start "declarator"
	// CMinus.g:76:1: declarator : ID -> {new StringTemplate($ID.text)};
	public final CMinusParser.declarator_return declarator() throws RecognitionException {
		CMinusParser.declarator_return retval = new CMinusParser.declarator_return();
		retval.start = input.LT(1);

		Token ID19=null;

		try {
			// CMinus.g:77:5: ( ID -> {new StringTemplate($ID.text)})
			// CMinus.g:77:9: ID
			{
			ID19=(Token)match(input,ID,FOLLOW_ID_in_declarator749); if (state.failed) return retval;
			// TEMPLATE REWRITE
			if ( state.backtracking==0 ) {
			  // 77:12: -> {new StringTemplate($ID.text)}
			  {
			  	retval.st = new StringTemplate((ID19!=null?ID19.getText():null));
			  }


			}

			}

			retval.stop = input.LT(-1);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "declarator"


	protected static class function_scope {
		String name;
	}
	protected Stack<function_scope> function_stack = new Stack<function_scope>();

	public static class function_return extends ParserRuleReturnScope {
		public StringTemplate st;
		public Object getTemplate() { return st; }
		public String toString() { return st==null?null:st.toString(); }
	};


	// $ANTLR start "function"
	// CMinus.g:80:1: function : type ID '(' (p+= formalParameter ( ',' p+= formalParameter )* )? ')' block -> function(type=$type.stname=$function::namelocals=$slist::localsstats=$slist::statsargs=$p);
	public final CMinusParser.function_return function() throws RecognitionException {
		slist_stack.push(new slist_scope());
		function_stack.push(new function_scope());
		CMinusParser.function_return retval = new CMinusParser.function_return();
		retval.start = input.LT(1);

		Token ID20=null;
		List<Object> list_p=null;
		ParserRuleReturnScope type21 =null;
		RuleReturnScope p = null;

		  slist_stack.peek().locals = new ArrayList();
		  slist_stack.peek().stats = new ArrayList();

		try {
			// CMinus.g:89:5: ( type ID '(' (p+= formalParameter ( ',' p+= formalParameter )* )? ')' block -> function(type=$type.stname=$function::namelocals=$slist::localsstats=$slist::statsargs=$p))
			// CMinus.g:89:9: type ID '(' (p+= formalParameter ( ',' p+= formalParameter )* )? ')' block
			{
			pushFollow(FOLLOW_type_in_function786);
			type21=type();
			state._fsp--;
			if (state.failed) return retval;
			ID20=(Token)match(input,ID,FOLLOW_ID_in_function788); if (state.failed) return retval;
			if ( state.backtracking==0 ) {function_stack.peek().name =(ID20!=null?ID20.getText():null);}
			match(input,11,FOLLOW_11_in_function800); if (state.failed) return retval;
			// CMinus.g:90:13: (p+= formalParameter ( ',' p+= formalParameter )* )?
			int alt10=2;
			int LA10_0 = input.LA(1);
			if ( (LA10_0==ID||LA10_0==52||LA10_0==55||LA10_0==60||LA10_0==64||LA10_0==72||LA10_0==88) ) {
				alt10=1;
			}
			switch (alt10) {
				case 1 :
					// CMinus.g:90:15: p+= formalParameter ( ',' p+= formalParameter )*
					{
					pushFollow(FOLLOW_formalParameter_in_function806);
					p=formalParameter();
					state._fsp--;
					if (state.failed) return retval;
					if (list_p==null) list_p=new ArrayList<Object>();
					list_p.add(p.getTemplate());
					// CMinus.g:90:34: ( ',' p+= formalParameter )*
					loop9:
					while (true) {
						int alt9=2;
						int LA9_0 = input.LA(1);
						if ( (LA9_0==18) ) {
							alt9=1;
						}

						switch (alt9) {
						case 1 :
							// CMinus.g:90:36: ',' p+= formalParameter
							{
							match(input,18,FOLLOW_18_in_function810); if (state.failed) return retval;
							pushFollow(FOLLOW_formalParameter_in_function814);
							p=formalParameter();
							state._fsp--;
							if (state.failed) return retval;
							if (list_p==null) list_p=new ArrayList<Object>();
							list_p.add(p.getTemplate());
							}
							break;

						default :
							break loop9;
						}
					}

					}
					break;

			}

			match(input,12,FOLLOW_12_in_function822); if (state.failed) return retval;
			pushFollow(FOLLOW_block_in_function832);
			block();
			state._fsp--;
			if (state.failed) return retval;
			// TEMPLATE REWRITE
			if ( state.backtracking==0 ) {
			  // 92:9: -> function(type=$type.stname=$function::namelocals=$slist::localsstats=$slist::statsargs=$p)
			  {
			  	retval.st = templateLib.getInstanceOf("function",new STAttrMap().put("type", (type21!=null?((StringTemplate)type21.getTemplate()):null)).put("name", function_stack.peek().name).put("locals", slist_stack.peek().locals).put("stats", slist_stack.peek().stats).put("args", list_p));
			  }


			}

			}

			retval.stop = input.LT(-1);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			slist_stack.pop();
			function_stack.pop();
		}
		return retval;
	}
	// $ANTLR end "function"


	public static class formalParameter_return extends ParserRuleReturnScope {
		public StringTemplate st;
		public Object getTemplate() { return st; }
		public String toString() { return st==null?null:st.toString(); }
	};


	// $ANTLR start "formalParameter"
	// CMinus.g:98:1: formalParameter : ( type declarator -> parameter(type=$type.stname=$declarator.st)| type declarator '[]' -> arrayparameter(type=$type.stname=$declarator.st)| type declarator '[][]' -> doublearrayparameter(type=$type.stname=$declarator.st));
	public final CMinusParser.formalParameter_return formalParameter() throws RecognitionException {
		CMinusParser.formalParameter_return retval = new CMinusParser.formalParameter_return();
		retval.start = input.LT(1);

		ParserRuleReturnScope type22 =null;
		ParserRuleReturnScope declarator23 =null;
		ParserRuleReturnScope type24 =null;
		ParserRuleReturnScope declarator25 =null;
		ParserRuleReturnScope type26 =null;
		ParserRuleReturnScope declarator27 =null;

		try {
			// CMinus.g:99:5: ( type declarator -> parameter(type=$type.stname=$declarator.st)| type declarator '[]' -> arrayparameter(type=$type.stname=$declarator.st)| type declarator '[][]' -> doublearrayparameter(type=$type.stname=$declarator.st))
			int alt11=3;
			switch ( input.LA(1) ) {
			case 88:
				{
				int LA11_1 = input.LA(2);
				if ( (LA11_1==72) ) {
					int LA11_8 = input.LA(3);
					if ( (LA11_8==72) ) {
						int LA11_12 = input.LA(4);
						if ( (LA11_12==64) ) {
							int LA11_17 = input.LA(5);
							if ( (LA11_17==ID) ) {
								switch ( input.LA(6) ) {
								case EOF:
								case 12:
								case 18:
									{
									alt11=1;
									}
									break;
								case 46:
									{
									alt11=2;
									}
									break;
								case 47:
									{
									alt11=3;
									}
									break;
								default:
									if (state.backtracking>0) {state.failed=true; return retval;}
									int nvaeMark = input.mark();
									try {
										for (int nvaeConsume = 0; nvaeConsume < 6 - 1; nvaeConsume++) {
											input.consume();
										}
										NoViableAltException nvae =
											new NoViableAltException("", 11, 11, input);
										throw nvae;
									} finally {
										input.rewind(nvaeMark);
									}
								}
							}

							else {
								if (state.backtracking>0) {state.failed=true; return retval;}
								int nvaeMark = input.mark();
								try {
									for (int nvaeConsume = 0; nvaeConsume < 5 - 1; nvaeConsume++) {
										input.consume();
									}
									NoViableAltException nvae =
										new NoViableAltException("", 11, 17, input);
									throw nvae;
								} finally {
									input.rewind(nvaeMark);
								}
							}

						}

						else {
							if (state.backtracking>0) {state.failed=true; return retval;}
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 11, 12, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

					}
					else if ( (LA11_8==ID) ) {
						switch ( input.LA(4) ) {
						case EOF:
						case 12:
						case 18:
							{
							alt11=1;
							}
							break;
						case 46:
							{
							alt11=2;
							}
							break;
						case 47:
							{
							alt11=3;
							}
							break;
						default:
							if (state.backtracking>0) {state.failed=true; return retval;}
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 11, 11, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}
					}

					else {
						if (state.backtracking>0) {state.failed=true; return retval;}
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 11, 8, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}

				}
				else if ( (LA11_1==64) ) {
					int LA11_9 = input.LA(3);
					if ( (LA11_9==ID) ) {
						switch ( input.LA(4) ) {
						case EOF:
						case 12:
						case 18:
							{
							alt11=1;
							}
							break;
						case 46:
							{
							alt11=2;
							}
							break;
						case 47:
							{
							alt11=3;
							}
							break;
						default:
							if (state.backtracking>0) {state.failed=true; return retval;}
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 11, 11, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}
					}

					else {
						if (state.backtracking>0) {state.failed=true; return retval;}
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 11, 9, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}

				}

				else {
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 11, 1, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case 72:
				{
				int LA11_2 = input.LA(2);
				if ( (LA11_2==72) ) {
					int LA11_10 = input.LA(3);
					if ( (LA11_10==64) ) {
						int LA11_13 = input.LA(4);
						if ( (LA11_13==ID) ) {
							switch ( input.LA(5) ) {
							case EOF:
							case 12:
							case 18:
								{
								alt11=1;
								}
								break;
							case 46:
								{
								alt11=2;
								}
								break;
							case 47:
								{
								alt11=3;
								}
								break;
							default:
								if (state.backtracking>0) {state.failed=true; return retval;}
								int nvaeMark = input.mark();
								try {
									for (int nvaeConsume = 0; nvaeConsume < 5 - 1; nvaeConsume++) {
										input.consume();
									}
									NoViableAltException nvae =
										new NoViableAltException("", 11, 11, input);
									throw nvae;
								} finally {
									input.rewind(nvaeMark);
								}
							}
						}

						else {
							if (state.backtracking>0) {state.failed=true; return retval;}
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 11, 13, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

					}

					else {
						if (state.backtracking>0) {state.failed=true; return retval;}
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 11, 10, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}

				}
				else if ( (LA11_2==ID) ) {
					switch ( input.LA(3) ) {
					case EOF:
					case 12:
					case 18:
						{
						alt11=1;
						}
						break;
					case 46:
						{
						alt11=2;
						}
						break;
					case 47:
						{
						alt11=3;
						}
						break;
					default:
						if (state.backtracking>0) {state.failed=true; return retval;}
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 11, 11, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}
				}

				else {
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 11, 2, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case 64:
				{
				int LA11_3 = input.LA(2);
				if ( (LA11_3==ID) ) {
					switch ( input.LA(3) ) {
					case EOF:
					case 12:
					case 18:
						{
						alt11=1;
						}
						break;
					case 46:
						{
						alt11=2;
						}
						break;
					case 47:
						{
						alt11=3;
						}
						break;
					default:
						if (state.backtracking>0) {state.failed=true; return retval;}
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 11, 11, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}
				}

				else {
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 11, 3, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case 52:
				{
				int LA11_4 = input.LA(2);
				if ( (LA11_4==ID) ) {
					switch ( input.LA(3) ) {
					case EOF:
					case 12:
					case 18:
						{
						alt11=1;
						}
						break;
					case 46:
						{
						alt11=2;
						}
						break;
					case 47:
						{
						alt11=3;
						}
						break;
					default:
						if (state.backtracking>0) {state.failed=true; return retval;}
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 11, 11, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}
				}

				else {
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 11, 4, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case 60:
				{
				int LA11_5 = input.LA(2);
				if ( (LA11_5==ID) ) {
					switch ( input.LA(3) ) {
					case EOF:
					case 12:
					case 18:
						{
						alt11=1;
						}
						break;
					case 46:
						{
						alt11=2;
						}
						break;
					case 47:
						{
						alt11=3;
						}
						break;
					default:
						if (state.backtracking>0) {state.failed=true; return retval;}
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 11, 11, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}
				}

				else {
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 11, 5, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case 55:
				{
				int LA11_6 = input.LA(2);
				if ( (LA11_6==ID) ) {
					switch ( input.LA(3) ) {
					case EOF:
					case 12:
					case 18:
						{
						alt11=1;
						}
						break;
					case 46:
						{
						alt11=2;
						}
						break;
					case 47:
						{
						alt11=3;
						}
						break;
					default:
						if (state.backtracking>0) {state.failed=true; return retval;}
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 11, 11, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}
				}

				else {
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 11, 6, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case ID:
				{
				int LA11_7 = input.LA(2);
				if ( (LA11_7==ID) ) {
					switch ( input.LA(3) ) {
					case EOF:
					case 12:
					case 18:
						{
						alt11=1;
						}
						break;
					case 46:
						{
						alt11=2;
						}
						break;
					case 47:
						{
						alt11=3;
						}
						break;
					default:
						if (state.backtracking>0) {state.failed=true; return retval;}
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 11, 11, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}
				}

				else {
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 11, 7, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			default:
				if (state.backtracking>0) {state.failed=true; return retval;}
				NoViableAltException nvae =
					new NoViableAltException("", 11, 0, input);
				throw nvae;
			}
			switch (alt11) {
				case 1 :
					// CMinus.g:99:9: type declarator
					{
					pushFollow(FOLLOW_type_in_formalParameter948);
					type22=type();
					state._fsp--;
					if (state.failed) return retval;
					pushFollow(FOLLOW_declarator_in_formalParameter950);
					declarator23=declarator();
					state._fsp--;
					if (state.failed) return retval;
					// TEMPLATE REWRITE
					if ( state.backtracking==0 ) {
					  // 100:9: -> parameter(type=$type.stname=$declarator.st)
					  {
					  	retval.st = templateLib.getInstanceOf("parameter",new STAttrMap().put("type", (type22!=null?((StringTemplate)type22.getTemplate()):null)).put("name", (declarator23!=null?((StringTemplate)declarator23.getTemplate()):null)));
					  }


					}

					}
					break;
				case 2 :
					// CMinus.g:101:6: type declarator '[]'
					{
					pushFollow(FOLLOW_type_in_formalParameter979);
					type24=type();
					state._fsp--;
					if (state.failed) return retval;
					pushFollow(FOLLOW_declarator_in_formalParameter981);
					declarator25=declarator();
					state._fsp--;
					if (state.failed) return retval;
					match(input,46,FOLLOW_46_in_formalParameter983); if (state.failed) return retval;
					// TEMPLATE REWRITE
					if ( state.backtracking==0 ) {
					  // 102:6: -> arrayparameter(type=$type.stname=$declarator.st)
					  {
					  	retval.st = templateLib.getInstanceOf("arrayparameter",new STAttrMap().put("type", (type24!=null?((StringTemplate)type24.getTemplate()):null)).put("name", (declarator25!=null?((StringTemplate)declarator25.getTemplate()):null)));
					  }


					}

					}
					break;
				case 3 :
					// CMinus.g:103:6: type declarator '[][]'
					{
					pushFollow(FOLLOW_type_in_formalParameter1008);
					type26=type();
					state._fsp--;
					if (state.failed) return retval;
					pushFollow(FOLLOW_declarator_in_formalParameter1010);
					declarator27=declarator();
					state._fsp--;
					if (state.failed) return retval;
					match(input,47,FOLLOW_47_in_formalParameter1012); if (state.failed) return retval;
					// TEMPLATE REWRITE
					if ( state.backtracking==0 ) {
					  // 104:6: -> doublearrayparameter(type=$type.stname=$declarator.st)
					  {
					  	retval.st = templateLib.getInstanceOf("doublearrayparameter",new STAttrMap().put("type", (type26!=null?((StringTemplate)type26.getTemplate()):null)).put("name", (declarator27!=null?((StringTemplate)declarator27.getTemplate()):null)));
					  }


					}

					}
					break;

			}
			retval.stop = input.LT(-1);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "formalParameter"


	public static class type_return extends ParserRuleReturnScope {
		public StringTemplate st;
		public Object getTemplate() { return st; }
		public String toString() { return st==null?null:st.toString(); }
	};


	// $ANTLR start "type"
	// CMinus.g:107:1: type : ( 'unsigned' 'long' 'long' 'int' -> type_unsigned_long_long_int(| 'long' 'long' 'int' -> type_long_long_int(| 'unsigned' 'long' -> type_unsigned_long_int(| 'unsigned' 'int' -> type_unsigned_int(| 'int' -> type_int(| 'long' -> type_long_int(| 'char' -> type_char(| 'float' -> type_float(| 'double' -> type_double(| ID -> type_user_object(name=$ID.text));
	public final CMinusParser.type_return type() throws RecognitionException {
		CMinusParser.type_return retval = new CMinusParser.type_return();
		retval.start = input.LT(1);

		Token ID28=null;

		try {
			// CMinus.g:108:2: ( 'unsigned' 'long' 'long' 'int' -> type_unsigned_long_long_int(| 'long' 'long' 'int' -> type_long_long_int(| 'unsigned' 'long' -> type_unsigned_long_int(| 'unsigned' 'int' -> type_unsigned_int(| 'int' -> type_int(| 'long' -> type_long_int(| 'char' -> type_char(| 'float' -> type_float(| 'double' -> type_double(| ID -> type_user_object(name=$ID.text))
			int alt12=10;
			switch ( input.LA(1) ) {
			case 88:
				{
				int LA12_1 = input.LA(2);
				if ( (LA12_1==72) ) {
					int LA12_8 = input.LA(3);
					if ( (LA12_8==72) ) {
						alt12=1;
					}
					else if ( (LA12_8==ID||LA12_8==12) ) {
						alt12=3;
					}

					else {
						if (state.backtracking>0) {state.failed=true; return retval;}
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 12, 8, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}

				}
				else if ( (LA12_1==64) ) {
					alt12=4;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 12, 1, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case 72:
				{
				int LA12_2 = input.LA(2);
				if ( (LA12_2==72) ) {
					alt12=2;
				}
				else if ( (LA12_2==ID||LA12_2==12) ) {
					alt12=6;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 12, 2, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case 64:
				{
				alt12=5;
				}
				break;
			case 52:
				{
				alt12=7;
				}
				break;
			case 60:
				{
				alt12=8;
				}
				break;
			case 55:
				{
				alt12=9;
				}
				break;
			case ID:
				{
				alt12=10;
				}
				break;
			default:
				if (state.backtracking>0) {state.failed=true; return retval;}
				NoViableAltException nvae =
					new NoViableAltException("", 12, 0, input);
				throw nvae;
			}
			switch (alt12) {
				case 1 :
					// CMinus.g:108:6: 'unsigned' 'long' 'long' 'int'
					{
					match(input,88,FOLLOW_88_in_type1046); if (state.failed) return retval;
					match(input,72,FOLLOW_72_in_type1048); if (state.failed) return retval;
					match(input,72,FOLLOW_72_in_type1050); if (state.failed) return retval;
					match(input,64,FOLLOW_64_in_type1052); if (state.failed) return retval;
					// TEMPLATE REWRITE
					if ( state.backtracking==0 ) {
					  // 108:37: -> type_unsigned_long_long_int(
					  {
					  	retval.st = templateLib.getInstanceOf("type_unsigned_long_long_int");
					  }


					}

					}
					break;
				case 2 :
					// CMinus.g:109:6: 'long' 'long' 'int'
					{
					match(input,72,FOLLOW_72_in_type1065); if (state.failed) return retval;
					match(input,72,FOLLOW_72_in_type1067); if (state.failed) return retval;
					match(input,64,FOLLOW_64_in_type1069); if (state.failed) return retval;
					// TEMPLATE REWRITE
					if ( state.backtracking==0 ) {
					  // 109:26: -> type_long_long_int(
					  {
					  	retval.st = templateLib.getInstanceOf("type_long_long_int");
					  }


					}

					}
					break;
				case 3 :
					// CMinus.g:110:6: 'unsigned' 'long'
					{
					match(input,88,FOLLOW_88_in_type1082); if (state.failed) return retval;
					match(input,72,FOLLOW_72_in_type1084); if (state.failed) return retval;
					// TEMPLATE REWRITE
					if ( state.backtracking==0 ) {
					  // 110:24: -> type_unsigned_long_int(
					  {
					  	retval.st = templateLib.getInstanceOf("type_unsigned_long_int");
					  }


					}

					}
					break;
				case 4 :
					// CMinus.g:111:6: 'unsigned' 'int'
					{
					match(input,88,FOLLOW_88_in_type1097); if (state.failed) return retval;
					match(input,64,FOLLOW_64_in_type1099); if (state.failed) return retval;
					// TEMPLATE REWRITE
					if ( state.backtracking==0 ) {
					  // 111:23: -> type_unsigned_int(
					  {
					  	retval.st = templateLib.getInstanceOf("type_unsigned_int");
					  }


					}

					}
					break;
				case 5 :
					// CMinus.g:112:6: 'int'
					{
					match(input,64,FOLLOW_64_in_type1112); if (state.failed) return retval;
					// TEMPLATE REWRITE
					if ( state.backtracking==0 ) {
					  // 112:13: -> type_int(
					  {
					  	retval.st = templateLib.getInstanceOf("type_int");
					  }


					}

					}
					break;
				case 6 :
					// CMinus.g:113:6: 'long'
					{
					match(input,72,FOLLOW_72_in_type1126); if (state.failed) return retval;
					// TEMPLATE REWRITE
					if ( state.backtracking==0 ) {
					  // 113:13: -> type_long_int(
					  {
					  	retval.st = templateLib.getInstanceOf("type_long_int");
					  }


					}

					}
					break;
				case 7 :
					// CMinus.g:114:9: 'char'
					{
					match(input,52,FOLLOW_52_in_type1142); if (state.failed) return retval;
					// TEMPLATE REWRITE
					if ( state.backtracking==0 ) {
					  // 114:16: -> type_char(
					  {
					  	retval.st = templateLib.getInstanceOf("type_char");
					  }


					}

					}
					break;
				case 8 :
					// CMinus.g:115:6: 'float'
					{
					match(input,60,FOLLOW_60_in_type1155); if (state.failed) return retval;
					// TEMPLATE REWRITE
					if ( state.backtracking==0 ) {
					  // 115:14: -> type_float(
					  {
					  	retval.st = templateLib.getInstanceOf("type_float");
					  }


					}

					}
					break;
				case 9 :
					// CMinus.g:116:6: 'double'
					{
					match(input,55,FOLLOW_55_in_type1168); if (state.failed) return retval;
					// TEMPLATE REWRITE
					if ( state.backtracking==0 ) {
					  // 116:15: -> type_double(
					  {
					  	retval.st = templateLib.getInstanceOf("type_double");
					  }


					}

					}
					break;
				case 10 :
					// CMinus.g:117:9: ID
					{
					ID28=(Token)match(input,ID,FOLLOW_ID_in_type1184); if (state.failed) return retval;
					// TEMPLATE REWRITE
					if ( state.backtracking==0 ) {
					  // 117:16: -> type_user_object(name=$ID.text)
					  {
					  	retval.st = templateLib.getInstanceOf("type_user_object",new STAttrMap().put("name", (ID28!=null?ID28.getText():null)));
					  }


					}

					}
					break;

			}
			retval.stop = input.LT(-1);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "type"


	public static class block_return extends ParserRuleReturnScope {
		public StringTemplate st;
		public Object getTemplate() { return st; }
		public String toString() { return st==null?null:st.toString(); }
	};


	// $ANTLR start "block"
	// CMinus.g:120:1: block : '{' ( variable | stat )* '}' ;
	public final CMinusParser.block_return block() throws RecognitionException {
		CMinusParser.block_return retval = new CMinusParser.block_return();
		retval.start = input.LT(1);

		ParserRuleReturnScope variable29 =null;
		ParserRuleReturnScope stat30 =null;

		try {
			// CMinus.g:121:5: ( '{' ( variable | stat )* '}' )
			// CMinus.g:121:8: '{' ( variable | stat )* '}'
			{
			match(input,89,FOLLOW_89_in_block1215); if (state.failed) return retval;
			// CMinus.g:122:8: ( variable | stat )*
			loop13:
			while (true) {
				int alt13=3;
				switch ( input.LA(1) ) {
				case 52:
				case 55:
				case 60:
				case 64:
				case 72:
				case 88:
					{
					alt13=1;
					}
					break;
				case ID:
					{
					int LA13_3 = input.LA(2);
					if ( (LA13_3==ID) ) {
						int LA13_5 = input.LA(3);
						if ( (LA13_5==11) ) {
							alt13=2;
						}
						else if ( (LA13_5==24||LA13_5==28||LA13_5==45) ) {
							alt13=1;
						}

					}
					else if ( ((LA13_3 >= 8 && LA13_3 <= 11)||(LA13_3 >= 13 && LA13_3 <= 17)||(LA13_3 >= 19 && LA13_3 <= 32)||LA13_3==45||LA13_3==49||(LA13_3 >= 90 && LA13_3 <= 91)) ) {
						alt13=2;
					}

					}
					break;
				case FP:
				case INT:
				case 11:
				case 19:
				case 24:
				case 50:
				case 51:
				case 53:
				case 54:
				case 56:
				case 57:
				case 59:
				case 61:
				case 62:
				case 63:
				case 70:
				case 71:
				case 74:
				case 75:
				case 76:
				case 77:
				case 78:
				case 79:
				case 80:
				case 81:
				case 82:
				case 89:
					{
					alt13=2;
					}
					break;
				}
				switch (alt13) {
				case 1 :
					// CMinus.g:122:10: variable
					{
					pushFollow(FOLLOW_variable_in_block1226);
					variable29=variable();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) {slist_stack.peek().locals.add((variable29!=null?((StringTemplate)variable29.getTemplate()):null));}
					}
					break;
				case 2 :
					// CMinus.g:122:57: stat
					{
					pushFollow(FOLLOW_stat_in_block1232);
					stat30=stat();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) {slist_stack.peek().stats.add((stat30!=null?((StringTemplate)stat30.getTemplate()):null));}
					}
					break;

				default :
					break loop13;
				}
			}

			match(input,92,FOLLOW_92_in_block1245); if (state.failed) return retval;
			}

			retval.stop = input.LT(-1);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "block"


	public static class stat_return extends ParserRuleReturnScope {
		public StringTemplate st;
		public Object getTemplate() { return st; }
		public String toString() { return st==null?null:st.toString(); }
	};


	// $ANTLR start "stat"
	// CMinus.g:127:1: stat : ( forStat -> {$forStat.st}| constructExpr ';' -> {$constructExpr.st}| assignStat ';' -> valueWithSemicolon(value=$assignStat.st)| ifStat -> {$ifStat.st}| elseStat -> {$elseStat.st}| expr ';' -> statement(expr=$expr.st)| block -> statementList(locals=$slist::localsstats=$slist::stats)| 'return' expr ';' -> return(expr=$expr.st)| ';' -> {new StringTemplate(\";\")});
	public final CMinusParser.stat_return stat() throws RecognitionException {
		slist_stack.push(new slist_scope());

		CMinusParser.stat_return retval = new CMinusParser.stat_return();
		retval.start = input.LT(1);

		ParserRuleReturnScope forStat31 =null;
		ParserRuleReturnScope constructExpr32 =null;
		ParserRuleReturnScope assignStat33 =null;
		ParserRuleReturnScope ifStat34 =null;
		ParserRuleReturnScope elseStat35 =null;
		ParserRuleReturnScope expr36 =null;
		ParserRuleReturnScope expr37 =null;


		  slist_stack.peek().locals = new ArrayList();
		  slist_stack.peek().stats = new ArrayList();

		try {
			// CMinus.g:133:5: ( forStat -> {$forStat.st}| constructExpr ';' -> {$constructExpr.st}| assignStat ';' -> valueWithSemicolon(value=$assignStat.st)| ifStat -> {$ifStat.st}| elseStat -> {$elseStat.st}| expr ';' -> statement(expr=$expr.st)| block -> statementList(locals=$slist::localsstats=$slist::stats)| 'return' expr ';' -> return(expr=$expr.st)| ';' -> {new StringTemplate(\";\")})
			int alt14=9;
			switch ( input.LA(1) ) {
			case 62:
				{
				alt14=1;
				}
				break;
			case ID:
				{
				int LA14_2 = input.LA(2);
				if ( (synpred29_CMinus()) ) {
					alt14=2;
				}
				else if ( (synpred30_CMinus()) ) {
					alt14=3;
				}
				else if ( (synpred33_CMinus()) ) {
					alt14=6;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 14, 2, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case 82:
				{
				int LA14_3 = input.LA(2);
				if ( (synpred30_CMinus()) ) {
					alt14=3;
				}
				else if ( (synpred33_CMinus()) ) {
					alt14=6;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 14, 3, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case 59:
				{
				int LA14_4 = input.LA(2);
				if ( (synpred30_CMinus()) ) {
					alt14=3;
				}
				else if ( (synpred33_CMinus()) ) {
					alt14=6;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 14, 4, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case 11:
				{
				int LA14_5 = input.LA(2);
				if ( (synpred30_CMinus()) ) {
					alt14=3;
				}
				else if ( (synpred33_CMinus()) ) {
					alt14=6;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 14, 5, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case 61:
				{
				int LA14_6 = input.LA(2);
				if ( (synpred30_CMinus()) ) {
					alt14=3;
				}
				else if ( (synpred33_CMinus()) ) {
					alt14=6;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 14, 6, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case 76:
				{
				int LA14_7 = input.LA(2);
				if ( (synpred30_CMinus()) ) {
					alt14=3;
				}
				else if ( (synpred33_CMinus()) ) {
					alt14=6;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 14, 7, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case 51:
				{
				int LA14_8 = input.LA(2);
				if ( (synpred30_CMinus()) ) {
					alt14=3;
				}
				else if ( (synpred33_CMinus()) ) {
					alt14=6;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 14, 8, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case 77:
				{
				int LA14_9 = input.LA(2);
				if ( (synpred30_CMinus()) ) {
					alt14=3;
				}
				else if ( (synpred33_CMinus()) ) {
					alt14=6;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 14, 9, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case 53:
				{
				int LA14_10 = input.LA(2);
				if ( (synpred30_CMinus()) ) {
					alt14=3;
				}
				else if ( (synpred33_CMinus()) ) {
					alt14=6;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 14, 10, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case 54:
				{
				int LA14_11 = input.LA(2);
				if ( (synpred30_CMinus()) ) {
					alt14=3;
				}
				else if ( (synpred33_CMinus()) ) {
					alt14=6;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 14, 11, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case 57:
				{
				int LA14_12 = input.LA(2);
				if ( (synpred30_CMinus()) ) {
					alt14=3;
				}
				else if ( (synpred33_CMinus()) ) {
					alt14=6;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 14, 12, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case 80:
				{
				int LA14_13 = input.LA(2);
				if ( (synpred30_CMinus()) ) {
					alt14=3;
				}
				else if ( (synpred33_CMinus()) ) {
					alt14=6;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 14, 13, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case 81:
				{
				int LA14_14 = input.LA(2);
				if ( (synpred30_CMinus()) ) {
					alt14=3;
				}
				else if ( (synpred33_CMinus()) ) {
					alt14=6;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 14, 14, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case 78:
				{
				int LA14_15 = input.LA(2);
				if ( (synpred30_CMinus()) ) {
					alt14=3;
				}
				else if ( (synpred33_CMinus()) ) {
					alt14=6;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 14, 15, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case 70:
				{
				int LA14_16 = input.LA(2);
				if ( (synpred30_CMinus()) ) {
					alt14=3;
				}
				else if ( (synpred33_CMinus()) ) {
					alt14=6;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 14, 16, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case 71:
				{
				int LA14_17 = input.LA(2);
				if ( (synpred30_CMinus()) ) {
					alt14=3;
				}
				else if ( (synpred33_CMinus()) ) {
					alt14=6;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 14, 17, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case 79:
				{
				int LA14_18 = input.LA(2);
				if ( (synpred30_CMinus()) ) {
					alt14=3;
				}
				else if ( (synpred33_CMinus()) ) {
					alt14=6;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 14, 18, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case 74:
				{
				int LA14_19 = input.LA(2);
				if ( (synpred30_CMinus()) ) {
					alt14=3;
				}
				else if ( (synpred33_CMinus()) ) {
					alt14=6;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 14, 19, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case 50:
				{
				int LA14_20 = input.LA(2);
				if ( (synpred30_CMinus()) ) {
					alt14=3;
				}
				else if ( (synpred33_CMinus()) ) {
					alt14=6;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 14, 20, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case FP:
				{
				int LA14_21 = input.LA(2);
				if ( (synpred30_CMinus()) ) {
					alt14=3;
				}
				else if ( (synpred33_CMinus()) ) {
					alt14=6;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 14, 21, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case INT:
				{
				int LA14_22 = input.LA(2);
				if ( (synpred30_CMinus()) ) {
					alt14=3;
				}
				else if ( (synpred33_CMinus()) ) {
					alt14=6;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 14, 22, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case 19:
				{
				int LA14_23 = input.LA(2);
				if ( (synpred30_CMinus()) ) {
					alt14=3;
				}
				else if ( (synpred33_CMinus()) ) {
					alt14=6;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 14, 23, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case 89:
				{
				int LA14_24 = input.LA(2);
				if ( (synpred30_CMinus()) ) {
					alt14=3;
				}
				else if ( (synpred33_CMinus()) ) {
					alt14=6;
				}
				else if ( (synpred34_CMinus()) ) {
					alt14=7;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 14, 24, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case 63:
				{
				alt14=4;
				}
				break;
			case 56:
				{
				alt14=5;
				}
				break;
			case 75:
				{
				alt14=8;
				}
				break;
			case 24:
				{
				alt14=9;
				}
				break;
			default:
				if (state.backtracking>0) {state.failed=true; return retval;}
				NoViableAltException nvae =
					new NoViableAltException("", 14, 0, input);
				throw nvae;
			}
			switch (alt14) {
				case 1 :
					// CMinus.g:133:7: forStat
					{
					pushFollow(FOLLOW_forStat_in_stat1274);
					forStat31=forStat();
					state._fsp--;
					if (state.failed) return retval;
					// TEMPLATE REWRITE
					if ( state.backtracking==0 ) {
					  // 133:15: -> {$forStat.st}
					  {
					  	retval.st = (forStat31!=null?((StringTemplate)forStat31.getTemplate()):null);
					  }


					}

					}
					break;
				case 2 :
					// CMinus.g:134:4: constructExpr ';'
					{
					pushFollow(FOLLOW_constructExpr_in_stat1283);
					constructExpr32=constructExpr();
					state._fsp--;
					if (state.failed) return retval;
					match(input,24,FOLLOW_24_in_stat1285); if (state.failed) return retval;
					// TEMPLATE REWRITE
					if ( state.backtracking==0 ) {
					  // 134:22: -> {$constructExpr.st}
					  {
					  	retval.st = (constructExpr32!=null?((StringTemplate)constructExpr32.getTemplate()):null);
					  }


					}

					}
					break;
				case 3 :
					// CMinus.g:135:4: assignStat ';'
					{
					pushFollow(FOLLOW_assignStat_in_stat1294);
					assignStat33=assignStat();
					state._fsp--;
					if (state.failed) return retval;
					match(input,24,FOLLOW_24_in_stat1296); if (state.failed) return retval;
					// TEMPLATE REWRITE
					if ( state.backtracking==0 ) {
					  // 135:19: -> valueWithSemicolon(value=$assignStat.st)
					  {
					  	retval.st = templateLib.getInstanceOf("valueWithSemicolon",new STAttrMap().put("value", (assignStat33!=null?((StringTemplate)assignStat33.getTemplate()):null)));
					  }


					}

					}
					break;
				case 4 :
					// CMinus.g:136:4: ifStat
					{
					pushFollow(FOLLOW_ifStat_in_stat1310);
					ifStat34=ifStat();
					state._fsp--;
					if (state.failed) return retval;
					// TEMPLATE REWRITE
					if ( state.backtracking==0 ) {
					  // 136:11: -> {$ifStat.st}
					  {
					  	retval.st = (ifStat34!=null?((StringTemplate)ifStat34.getTemplate()):null);
					  }


					}

					}
					break;
				case 5 :
					// CMinus.g:137:4: elseStat
					{
					pushFollow(FOLLOW_elseStat_in_stat1319);
					elseStat35=elseStat();
					state._fsp--;
					if (state.failed) return retval;
					// TEMPLATE REWRITE
					if ( state.backtracking==0 ) {
					  // 137:13: -> {$elseStat.st}
					  {
					  	retval.st = (elseStat35!=null?((StringTemplate)elseStat35.getTemplate()):null);
					  }


					}

					}
					break;
				case 6 :
					// CMinus.g:138:7: expr ';'
					{
					pushFollow(FOLLOW_expr_in_stat1331);
					expr36=expr();
					state._fsp--;
					if (state.failed) return retval;
					match(input,24,FOLLOW_24_in_stat1333); if (state.failed) return retval;
					// TEMPLATE REWRITE
					if ( state.backtracking==0 ) {
					  // 138:16: -> statement(expr=$expr.st)
					  {
					  	retval.st = templateLib.getInstanceOf("statement",new STAttrMap().put("expr", (expr36!=null?((StringTemplate)expr36.getTemplate()):null)));
					  }


					}

					}
					break;
				case 7 :
					// CMinus.g:139:7: block
					{
					pushFollow(FOLLOW_block_in_stat1350);
					block();
					state._fsp--;
					if (state.failed) return retval;
					// TEMPLATE REWRITE
					if ( state.backtracking==0 ) {
					  // 139:13: -> statementList(locals=$slist::localsstats=$slist::stats)
					  {
					  	retval.st = templateLib.getInstanceOf("statementList",new STAttrMap().put("locals", slist_stack.peek().locals).put("stats", slist_stack.peek().stats));
					  }


					}

					}
					break;
				case 8 :
					// CMinus.g:140:4: 'return' expr ';'
					{
					match(input,75,FOLLOW_75_in_stat1369); if (state.failed) return retval;
					pushFollow(FOLLOW_expr_in_stat1371);
					expr37=expr();
					state._fsp--;
					if (state.failed) return retval;
					match(input,24,FOLLOW_24_in_stat1373); if (state.failed) return retval;
					// TEMPLATE REWRITE
					if ( state.backtracking==0 ) {
					  // 140:22: -> return(expr=$expr.st)
					  {
					  	retval.st = templateLib.getInstanceOf("return",new STAttrMap().put("expr", (expr37!=null?((StringTemplate)expr37.getTemplate()):null)));
					  }


					}

					}
					break;
				case 9 :
					// CMinus.g:141:7: ';'
					{
					match(input,24,FOLLOW_24_in_stat1390); if (state.failed) return retval;
					// TEMPLATE REWRITE
					if ( state.backtracking==0 ) {
					  // 141:11: -> {new StringTemplate(\";\")}
					  {
					  	retval.st = new StringTemplate(";");
					  }


					}

					}
					break;

			}
			retval.stop = input.LT(-1);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			slist_stack.pop();

		}
		return retval;
	}
	// $ANTLR end "stat"


	public static class ifStat_return extends ParserRuleReturnScope {
		public StringTemplate st;
		public Object getTemplate() { return st; }
		public String toString() { return st==null?null:st.toString(); }
	};


	// $ANTLR start "ifStat"
	// CMinus.g:144:1: ifStat : 'if' '(' e= condExpr ')' block -> ifBlock(e=$e.stlocals=$slist::localsstats=$slist::stats);
	public final CMinusParser.ifStat_return ifStat() throws RecognitionException {
		slist_stack.push(new slist_scope());

		CMinusParser.ifStat_return retval = new CMinusParser.ifStat_return();
		retval.start = input.LT(1);

		ParserRuleReturnScope e =null;


		  slist_stack.peek().locals = new ArrayList();
		  slist_stack.peek().stats = new ArrayList();

		try {
			// CMinus.g:150:5: ( 'if' '(' e= condExpr ')' block -> ifBlock(e=$e.stlocals=$slist::localsstats=$slist::stats))
			// CMinus.g:150:9: 'if' '(' e= condExpr ')' block
			{
			match(input,63,FOLLOW_63_in_ifStat1424); if (state.failed) return retval;
			match(input,11,FOLLOW_11_in_ifStat1426); if (state.failed) return retval;
			pushFollow(FOLLOW_condExpr_in_ifStat1430);
			e=condExpr();
			state._fsp--;
			if (state.failed) return retval;
			match(input,12,FOLLOW_12_in_ifStat1432); if (state.failed) return retval;
			pushFollow(FOLLOW_block_in_ifStat1434);
			block();
			state._fsp--;
			if (state.failed) return retval;
			// TEMPLATE REWRITE
			if ( state.backtracking==0 ) {
			  // 151:9: -> ifBlock(e=$e.stlocals=$slist::localsstats=$slist::stats)
			  {
			  	retval.st = templateLib.getInstanceOf("ifBlock",new STAttrMap().put("e", (e!=null?((StringTemplate)e.getTemplate()):null)).put("locals", slist_stack.peek().locals).put("stats", slist_stack.peek().stats));
			  }


			}

			}

			retval.stop = input.LT(-1);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			slist_stack.pop();

		}
		return retval;
	}
	// $ANTLR end "ifStat"


	public static class elseStat_return extends ParserRuleReturnScope {
		public StringTemplate st;
		public Object getTemplate() { return st; }
		public String toString() { return st==null?null:st.toString(); }
	};


	// $ANTLR start "elseStat"
	// CMinus.g:155:1: elseStat : 'else' block -> elseBlock(locals=$slist::localsstats=$slist::stats);
	public final CMinusParser.elseStat_return elseStat() throws RecognitionException {
		slist_stack.push(new slist_scope());

		CMinusParser.elseStat_return retval = new CMinusParser.elseStat_return();
		retval.start = input.LT(1);


		  slist_stack.peek().locals = new ArrayList();
		  slist_stack.peek().stats = new ArrayList();

		try {
			// CMinus.g:161:5: ( 'else' block -> elseBlock(locals=$slist::localsstats=$slist::stats))
			// CMinus.g:161:9: 'else' block
			{
			match(input,56,FOLLOW_56_in_elseStat1510); if (state.failed) return retval;
			pushFollow(FOLLOW_block_in_elseStat1512);
			block();
			state._fsp--;
			if (state.failed) return retval;
			// TEMPLATE REWRITE
			if ( state.backtracking==0 ) {
			  // 162:9: -> elseBlock(locals=$slist::localsstats=$slist::stats)
			  {
			  	retval.st = templateLib.getInstanceOf("elseBlock",new STAttrMap().put("locals", slist_stack.peek().locals).put("stats", slist_stack.peek().stats));
			  }


			}

			}

			retval.stop = input.LT(-1);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			slist_stack.pop();

		}
		return retval;
	}
	// $ANTLR end "elseStat"


	public static class forStat_return extends ParserRuleReturnScope {
		public StringTemplate st;
		public Object getTemplate() { return st; }
		public String toString() { return st==null?null:st.toString(); }
	};


	// $ANTLR start "forStat"
	// CMinus.g:165:1: forStat : 'for' '(' e1= assignStat ';' e2= expr ';' e3= assignStat ')' block -> forLoop(e1=$e1.ste2=$e2.ste3=$e3.stlocals=$slist::localsstats=$slist::stats);
	public final CMinusParser.forStat_return forStat() throws RecognitionException {
		slist_stack.push(new slist_scope());

		CMinusParser.forStat_return retval = new CMinusParser.forStat_return();
		retval.start = input.LT(1);

		ParserRuleReturnScope e1 =null;
		ParserRuleReturnScope e2 =null;
		ParserRuleReturnScope e3 =null;


		  slist_stack.peek().locals = new ArrayList();
		  slist_stack.peek().stats = new ArrayList();

		try {
			// CMinus.g:171:5: ( 'for' '(' e1= assignStat ';' e2= expr ';' e3= assignStat ')' block -> forLoop(e1=$e1.ste2=$e2.ste3=$e3.stlocals=$slist::localsstats=$slist::stats))
			// CMinus.g:171:9: 'for' '(' e1= assignStat ';' e2= expr ';' e3= assignStat ')' block
			{
			match(input,62,FOLLOW_62_in_forStat1563); if (state.failed) return retval;
			match(input,11,FOLLOW_11_in_forStat1565); if (state.failed) return retval;
			pushFollow(FOLLOW_assignStat_in_forStat1569);
			e1=assignStat();
			state._fsp--;
			if (state.failed) return retval;
			match(input,24,FOLLOW_24_in_forStat1571); if (state.failed) return retval;
			pushFollow(FOLLOW_expr_in_forStat1575);
			e2=expr();
			state._fsp--;
			if (state.failed) return retval;
			match(input,24,FOLLOW_24_in_forStat1577); if (state.failed) return retval;
			pushFollow(FOLLOW_assignStat_in_forStat1581);
			e3=assignStat();
			state._fsp--;
			if (state.failed) return retval;
			match(input,12,FOLLOW_12_in_forStat1583); if (state.failed) return retval;
			pushFollow(FOLLOW_block_in_forStat1585);
			block();
			state._fsp--;
			if (state.failed) return retval;
			// TEMPLATE REWRITE
			if ( state.backtracking==0 ) {
			  // 172:9: -> forLoop(e1=$e1.ste2=$e2.ste3=$e3.stlocals=$slist::localsstats=$slist::stats)
			  {
			  	retval.st = templateLib.getInstanceOf("forLoop",new STAttrMap().put("e1", (e1!=null?((StringTemplate)e1.getTemplate()):null)).put("e2", (e2!=null?((StringTemplate)e2.getTemplate()):null)).put("e3", (e3!=null?((StringTemplate)e3.getTemplate()):null)).put("locals", slist_stack.peek().locals).put("stats", slist_stack.peek().stats));
			  }


			}

			}

			retval.stop = input.LT(-1);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
			slist_stack.pop();

		}
		return retval;
	}
	// $ANTLR end "forStat"


	public static class assignStat_return extends ParserRuleReturnScope {
		public StringTemplate st;
		public Object getTemplate() { return st; }
		public String toString() { return st==null?null:st.toString(); }
	};


	// $ANTLR start "assignStat"
	// CMinus.g:176:1: assignStat : (e1= expr '=' e2= expr -> assign(lhs=$e1.strhs=$e2.st)| expr '++' -> inkrement(e=$expr.st)| expr '--' -> dekrement(e=$expr.st)|e1= expr '+=' e2= expr -> inkrementby(lhs=$e1.strhs=$e2.st)|e1= expr '-=' e2= expr -> dekrementby(lhs=$e1.strhs=$e2.st)|e1= expr '*=' e2= expr -> assignmultiply(lhs=$e1.strhs=$e2.st)|e1= expr '/=' e2= expr -> assigndivide(lhs=$e1.strhs=$e2.st));
	public final CMinusParser.assignStat_return assignStat() throws RecognitionException {
		CMinusParser.assignStat_return retval = new CMinusParser.assignStat_return();
		retval.start = input.LT(1);

		ParserRuleReturnScope e1 =null;
		ParserRuleReturnScope e2 =null;
		ParserRuleReturnScope expr38 =null;
		ParserRuleReturnScope expr39 =null;

		try {
			// CMinus.g:177:2: (e1= expr '=' e2= expr -> assign(lhs=$e1.strhs=$e2.st)| expr '++' -> inkrement(e=$expr.st)| expr '--' -> dekrement(e=$expr.st)|e1= expr '+=' e2= expr -> inkrementby(lhs=$e1.strhs=$e2.st)|e1= expr '-=' e2= expr -> dekrementby(lhs=$e1.strhs=$e2.st)|e1= expr '*=' e2= expr -> assignmultiply(lhs=$e1.strhs=$e2.st)|e1= expr '/=' e2= expr -> assigndivide(lhs=$e1.strhs=$e2.st))
			int alt15=7;
			switch ( input.LA(1) ) {
			case 82:
				{
				int LA15_1 = input.LA(2);
				if ( (synpred36_CMinus()) ) {
					alt15=1;
				}
				else if ( (synpred37_CMinus()) ) {
					alt15=2;
				}
				else if ( (synpred38_CMinus()) ) {
					alt15=3;
				}
				else if ( (synpred39_CMinus()) ) {
					alt15=4;
				}
				else if ( (synpred40_CMinus()) ) {
					alt15=5;
				}
				else if ( (synpred41_CMinus()) ) {
					alt15=6;
				}
				else if ( (true) ) {
					alt15=7;
				}

				}
				break;
			case 59:
				{
				int LA15_2 = input.LA(2);
				if ( (synpred36_CMinus()) ) {
					alt15=1;
				}
				else if ( (synpred37_CMinus()) ) {
					alt15=2;
				}
				else if ( (synpred38_CMinus()) ) {
					alt15=3;
				}
				else if ( (synpred39_CMinus()) ) {
					alt15=4;
				}
				else if ( (synpred40_CMinus()) ) {
					alt15=5;
				}
				else if ( (synpred41_CMinus()) ) {
					alt15=6;
				}
				else if ( (true) ) {
					alt15=7;
				}

				}
				break;
			case 11:
				{
				int LA15_3 = input.LA(2);
				if ( (synpred36_CMinus()) ) {
					alt15=1;
				}
				else if ( (synpred37_CMinus()) ) {
					alt15=2;
				}
				else if ( (synpred38_CMinus()) ) {
					alt15=3;
				}
				else if ( (synpred39_CMinus()) ) {
					alt15=4;
				}
				else if ( (synpred40_CMinus()) ) {
					alt15=5;
				}
				else if ( (synpred41_CMinus()) ) {
					alt15=6;
				}
				else if ( (true) ) {
					alt15=7;
				}

				}
				break;
			case ID:
				{
				int LA15_4 = input.LA(2);
				if ( (synpred36_CMinus()) ) {
					alt15=1;
				}
				else if ( (synpred37_CMinus()) ) {
					alt15=2;
				}
				else if ( (synpred38_CMinus()) ) {
					alt15=3;
				}
				else if ( (synpred39_CMinus()) ) {
					alt15=4;
				}
				else if ( (synpred40_CMinus()) ) {
					alt15=5;
				}
				else if ( (synpred41_CMinus()) ) {
					alt15=6;
				}
				else if ( (true) ) {
					alt15=7;
				}

				}
				break;
			case 61:
				{
				int LA15_5 = input.LA(2);
				if ( (synpred36_CMinus()) ) {
					alt15=1;
				}
				else if ( (synpred37_CMinus()) ) {
					alt15=2;
				}
				else if ( (synpred38_CMinus()) ) {
					alt15=3;
				}
				else if ( (synpred39_CMinus()) ) {
					alt15=4;
				}
				else if ( (synpred40_CMinus()) ) {
					alt15=5;
				}
				else if ( (synpred41_CMinus()) ) {
					alt15=6;
				}
				else if ( (true) ) {
					alt15=7;
				}

				}
				break;
			case 76:
				{
				int LA15_6 = input.LA(2);
				if ( (synpred36_CMinus()) ) {
					alt15=1;
				}
				else if ( (synpred37_CMinus()) ) {
					alt15=2;
				}
				else if ( (synpred38_CMinus()) ) {
					alt15=3;
				}
				else if ( (synpred39_CMinus()) ) {
					alt15=4;
				}
				else if ( (synpred40_CMinus()) ) {
					alt15=5;
				}
				else if ( (synpred41_CMinus()) ) {
					alt15=6;
				}
				else if ( (true) ) {
					alt15=7;
				}

				}
				break;
			case 51:
				{
				int LA15_7 = input.LA(2);
				if ( (synpred36_CMinus()) ) {
					alt15=1;
				}
				else if ( (synpred37_CMinus()) ) {
					alt15=2;
				}
				else if ( (synpred38_CMinus()) ) {
					alt15=3;
				}
				else if ( (synpred39_CMinus()) ) {
					alt15=4;
				}
				else if ( (synpred40_CMinus()) ) {
					alt15=5;
				}
				else if ( (synpred41_CMinus()) ) {
					alt15=6;
				}
				else if ( (true) ) {
					alt15=7;
				}

				}
				break;
			case 77:
				{
				int LA15_8 = input.LA(2);
				if ( (synpred36_CMinus()) ) {
					alt15=1;
				}
				else if ( (synpred37_CMinus()) ) {
					alt15=2;
				}
				else if ( (synpred38_CMinus()) ) {
					alt15=3;
				}
				else if ( (synpred39_CMinus()) ) {
					alt15=4;
				}
				else if ( (synpred40_CMinus()) ) {
					alt15=5;
				}
				else if ( (synpred41_CMinus()) ) {
					alt15=6;
				}
				else if ( (true) ) {
					alt15=7;
				}

				}
				break;
			case 53:
				{
				int LA15_9 = input.LA(2);
				if ( (synpred36_CMinus()) ) {
					alt15=1;
				}
				else if ( (synpred37_CMinus()) ) {
					alt15=2;
				}
				else if ( (synpred38_CMinus()) ) {
					alt15=3;
				}
				else if ( (synpred39_CMinus()) ) {
					alt15=4;
				}
				else if ( (synpred40_CMinus()) ) {
					alt15=5;
				}
				else if ( (synpred41_CMinus()) ) {
					alt15=6;
				}
				else if ( (true) ) {
					alt15=7;
				}

				}
				break;
			case 54:
				{
				int LA15_10 = input.LA(2);
				if ( (synpred36_CMinus()) ) {
					alt15=1;
				}
				else if ( (synpred37_CMinus()) ) {
					alt15=2;
				}
				else if ( (synpred38_CMinus()) ) {
					alt15=3;
				}
				else if ( (synpred39_CMinus()) ) {
					alt15=4;
				}
				else if ( (synpred40_CMinus()) ) {
					alt15=5;
				}
				else if ( (synpred41_CMinus()) ) {
					alt15=6;
				}
				else if ( (true) ) {
					alt15=7;
				}

				}
				break;
			case 57:
				{
				int LA15_11 = input.LA(2);
				if ( (synpred36_CMinus()) ) {
					alt15=1;
				}
				else if ( (synpred37_CMinus()) ) {
					alt15=2;
				}
				else if ( (synpred38_CMinus()) ) {
					alt15=3;
				}
				else if ( (synpred39_CMinus()) ) {
					alt15=4;
				}
				else if ( (synpred40_CMinus()) ) {
					alt15=5;
				}
				else if ( (synpred41_CMinus()) ) {
					alt15=6;
				}
				else if ( (true) ) {
					alt15=7;
				}

				}
				break;
			case 80:
				{
				int LA15_12 = input.LA(2);
				if ( (synpred36_CMinus()) ) {
					alt15=1;
				}
				else if ( (synpred37_CMinus()) ) {
					alt15=2;
				}
				else if ( (synpred38_CMinus()) ) {
					alt15=3;
				}
				else if ( (synpred39_CMinus()) ) {
					alt15=4;
				}
				else if ( (synpred40_CMinus()) ) {
					alt15=5;
				}
				else if ( (synpred41_CMinus()) ) {
					alt15=6;
				}
				else if ( (true) ) {
					alt15=7;
				}

				}
				break;
			case 81:
				{
				int LA15_13 = input.LA(2);
				if ( (synpred36_CMinus()) ) {
					alt15=1;
				}
				else if ( (synpred37_CMinus()) ) {
					alt15=2;
				}
				else if ( (synpred38_CMinus()) ) {
					alt15=3;
				}
				else if ( (synpred39_CMinus()) ) {
					alt15=4;
				}
				else if ( (synpred40_CMinus()) ) {
					alt15=5;
				}
				else if ( (synpred41_CMinus()) ) {
					alt15=6;
				}
				else if ( (true) ) {
					alt15=7;
				}

				}
				break;
			case 78:
				{
				int LA15_14 = input.LA(2);
				if ( (synpred36_CMinus()) ) {
					alt15=1;
				}
				else if ( (synpred37_CMinus()) ) {
					alt15=2;
				}
				else if ( (synpred38_CMinus()) ) {
					alt15=3;
				}
				else if ( (synpred39_CMinus()) ) {
					alt15=4;
				}
				else if ( (synpred40_CMinus()) ) {
					alt15=5;
				}
				else if ( (synpred41_CMinus()) ) {
					alt15=6;
				}
				else if ( (true) ) {
					alt15=7;
				}

				}
				break;
			case 70:
				{
				int LA15_15 = input.LA(2);
				if ( (synpred36_CMinus()) ) {
					alt15=1;
				}
				else if ( (synpred37_CMinus()) ) {
					alt15=2;
				}
				else if ( (synpred38_CMinus()) ) {
					alt15=3;
				}
				else if ( (synpred39_CMinus()) ) {
					alt15=4;
				}
				else if ( (synpred40_CMinus()) ) {
					alt15=5;
				}
				else if ( (synpred41_CMinus()) ) {
					alt15=6;
				}
				else if ( (true) ) {
					alt15=7;
				}

				}
				break;
			case 71:
				{
				int LA15_16 = input.LA(2);
				if ( (synpred36_CMinus()) ) {
					alt15=1;
				}
				else if ( (synpred37_CMinus()) ) {
					alt15=2;
				}
				else if ( (synpred38_CMinus()) ) {
					alt15=3;
				}
				else if ( (synpred39_CMinus()) ) {
					alt15=4;
				}
				else if ( (synpred40_CMinus()) ) {
					alt15=5;
				}
				else if ( (synpred41_CMinus()) ) {
					alt15=6;
				}
				else if ( (true) ) {
					alt15=7;
				}

				}
				break;
			case 79:
				{
				int LA15_17 = input.LA(2);
				if ( (synpred36_CMinus()) ) {
					alt15=1;
				}
				else if ( (synpred37_CMinus()) ) {
					alt15=2;
				}
				else if ( (synpred38_CMinus()) ) {
					alt15=3;
				}
				else if ( (synpred39_CMinus()) ) {
					alt15=4;
				}
				else if ( (synpred40_CMinus()) ) {
					alt15=5;
				}
				else if ( (synpred41_CMinus()) ) {
					alt15=6;
				}
				else if ( (true) ) {
					alt15=7;
				}

				}
				break;
			case 74:
				{
				int LA15_18 = input.LA(2);
				if ( (synpred36_CMinus()) ) {
					alt15=1;
				}
				else if ( (synpred37_CMinus()) ) {
					alt15=2;
				}
				else if ( (synpred38_CMinus()) ) {
					alt15=3;
				}
				else if ( (synpred39_CMinus()) ) {
					alt15=4;
				}
				else if ( (synpred40_CMinus()) ) {
					alt15=5;
				}
				else if ( (synpred41_CMinus()) ) {
					alt15=6;
				}
				else if ( (true) ) {
					alt15=7;
				}

				}
				break;
			case 50:
				{
				int LA15_19 = input.LA(2);
				if ( (synpred36_CMinus()) ) {
					alt15=1;
				}
				else if ( (synpred37_CMinus()) ) {
					alt15=2;
				}
				else if ( (synpred38_CMinus()) ) {
					alt15=3;
				}
				else if ( (synpred39_CMinus()) ) {
					alt15=4;
				}
				else if ( (synpred40_CMinus()) ) {
					alt15=5;
				}
				else if ( (synpred41_CMinus()) ) {
					alt15=6;
				}
				else if ( (true) ) {
					alt15=7;
				}

				}
				break;
			case FP:
				{
				int LA15_20 = input.LA(2);
				if ( (synpred36_CMinus()) ) {
					alt15=1;
				}
				else if ( (synpred37_CMinus()) ) {
					alt15=2;
				}
				else if ( (synpred38_CMinus()) ) {
					alt15=3;
				}
				else if ( (synpred39_CMinus()) ) {
					alt15=4;
				}
				else if ( (synpred40_CMinus()) ) {
					alt15=5;
				}
				else if ( (synpred41_CMinus()) ) {
					alt15=6;
				}
				else if ( (true) ) {
					alt15=7;
				}

				}
				break;
			case INT:
				{
				int LA15_21 = input.LA(2);
				if ( (synpred36_CMinus()) ) {
					alt15=1;
				}
				else if ( (synpred37_CMinus()) ) {
					alt15=2;
				}
				else if ( (synpred38_CMinus()) ) {
					alt15=3;
				}
				else if ( (synpred39_CMinus()) ) {
					alt15=4;
				}
				else if ( (synpred40_CMinus()) ) {
					alt15=5;
				}
				else if ( (synpred41_CMinus()) ) {
					alt15=6;
				}
				else if ( (true) ) {
					alt15=7;
				}

				}
				break;
			case 19:
				{
				int LA15_22 = input.LA(2);
				if ( (synpred36_CMinus()) ) {
					alt15=1;
				}
				else if ( (synpred37_CMinus()) ) {
					alt15=2;
				}
				else if ( (synpred38_CMinus()) ) {
					alt15=3;
				}
				else if ( (synpred39_CMinus()) ) {
					alt15=4;
				}
				else if ( (synpred40_CMinus()) ) {
					alt15=5;
				}
				else if ( (synpred41_CMinus()) ) {
					alt15=6;
				}
				else if ( (true) ) {
					alt15=7;
				}

				}
				break;
			case 89:
				{
				int LA15_23 = input.LA(2);
				if ( (synpred36_CMinus()) ) {
					alt15=1;
				}
				else if ( (synpred37_CMinus()) ) {
					alt15=2;
				}
				else if ( (synpred38_CMinus()) ) {
					alt15=3;
				}
				else if ( (synpred39_CMinus()) ) {
					alt15=4;
				}
				else if ( (synpred40_CMinus()) ) {
					alt15=5;
				}
				else if ( (synpred41_CMinus()) ) {
					alt15=6;
				}
				else if ( (true) ) {
					alt15=7;
				}

				}
				break;
			default:
				if (state.backtracking>0) {state.failed=true; return retval;}
				NoViableAltException nvae =
					new NoViableAltException("", 15, 0, input);
				throw nvae;
			}
			switch (alt15) {
				case 1 :
					// CMinus.g:177:6: e1= expr '=' e2= expr
					{
					pushFollow(FOLLOW_expr_in_assignStat1657);
					e1=expr();
					state._fsp--;
					if (state.failed) return retval;
					match(input,28,FOLLOW_28_in_assignStat1659); if (state.failed) return retval;
					pushFollow(FOLLOW_expr_in_assignStat1663);
					e2=expr();
					state._fsp--;
					if (state.failed) return retval;
					// TEMPLATE REWRITE
					if ( state.backtracking==0 ) {
					  // 177:26: -> assign(lhs=$e1.strhs=$e2.st)
					  {
					  	retval.st = templateLib.getInstanceOf("assign",new STAttrMap().put("lhs", (e1!=null?((StringTemplate)e1.getTemplate()):null)).put("rhs", (e2!=null?((StringTemplate)e2.getTemplate()):null)));
					  }


					}

					}
					break;
				case 2 :
					// CMinus.g:178:6: expr '++'
					{
					pushFollow(FOLLOW_expr_in_assignStat1684);
					expr38=expr();
					state._fsp--;
					if (state.failed) return retval;
					match(input,16,FOLLOW_16_in_assignStat1686); if (state.failed) return retval;
					// TEMPLATE REWRITE
					if ( state.backtracking==0 ) {
					  // 178:16: -> inkrement(e=$expr.st)
					  {
					  	retval.st = templateLib.getInstanceOf("inkrement",new STAttrMap().put("e", (expr38!=null?((StringTemplate)expr38.getTemplate()):null)));
					  }


					}

					}
					break;
				case 3 :
					// CMinus.g:179:6: expr '--'
					{
					pushFollow(FOLLOW_expr_in_assignStat1702);
					expr39=expr();
					state._fsp--;
					if (state.failed) return retval;
					match(input,20,FOLLOW_20_in_assignStat1704); if (state.failed) return retval;
					// TEMPLATE REWRITE
					if ( state.backtracking==0 ) {
					  // 179:16: -> dekrement(e=$expr.st)
					  {
					  	retval.st = templateLib.getInstanceOf("dekrement",new STAttrMap().put("e", (expr39!=null?((StringTemplate)expr39.getTemplate()):null)));
					  }


					}

					}
					break;
				case 4 :
					// CMinus.g:180:6: e1= expr '+=' e2= expr
					{
					pushFollow(FOLLOW_expr_in_assignStat1722);
					e1=expr();
					state._fsp--;
					if (state.failed) return retval;
					match(input,17,FOLLOW_17_in_assignStat1724); if (state.failed) return retval;
					pushFollow(FOLLOW_expr_in_assignStat1728);
					e2=expr();
					state._fsp--;
					if (state.failed) return retval;
					// TEMPLATE REWRITE
					if ( state.backtracking==0 ) {
					  // 180:27: -> inkrementby(lhs=$e1.strhs=$e2.st)
					  {
					  	retval.st = templateLib.getInstanceOf("inkrementby",new STAttrMap().put("lhs", (e1!=null?((StringTemplate)e1.getTemplate()):null)).put("rhs", (e2!=null?((StringTemplate)e2.getTemplate()):null)));
					  }


					}

					}
					break;
				case 5 :
					// CMinus.g:181:6: e1= expr '-=' e2= expr
					{
					pushFollow(FOLLOW_expr_in_assignStat1751);
					e1=expr();
					state._fsp--;
					if (state.failed) return retval;
					match(input,21,FOLLOW_21_in_assignStat1753); if (state.failed) return retval;
					pushFollow(FOLLOW_expr_in_assignStat1757);
					e2=expr();
					state._fsp--;
					if (state.failed) return retval;
					// TEMPLATE REWRITE
					if ( state.backtracking==0 ) {
					  // 181:27: -> dekrementby(lhs=$e1.strhs=$e2.st)
					  {
					  	retval.st = templateLib.getInstanceOf("dekrementby",new STAttrMap().put("lhs", (e1!=null?((StringTemplate)e1.getTemplate()):null)).put("rhs", (e2!=null?((StringTemplate)e2.getTemplate()):null)));
					  }


					}

					}
					break;
				case 6 :
					// CMinus.g:182:6: e1= expr '*=' e2= expr
					{
					pushFollow(FOLLOW_expr_in_assignStat1780);
					e1=expr();
					state._fsp--;
					if (state.failed) return retval;
					match(input,14,FOLLOW_14_in_assignStat1782); if (state.failed) return retval;
					pushFollow(FOLLOW_expr_in_assignStat1786);
					e2=expr();
					state._fsp--;
					if (state.failed) return retval;
					// TEMPLATE REWRITE
					if ( state.backtracking==0 ) {
					  // 182:27: -> assignmultiply(lhs=$e1.strhs=$e2.st)
					  {
					  	retval.st = templateLib.getInstanceOf("assignmultiply",new STAttrMap().put("lhs", (e1!=null?((StringTemplate)e1.getTemplate()):null)).put("rhs", (e2!=null?((StringTemplate)e2.getTemplate()):null)));
					  }


					}

					}
					break;
				case 7 :
					// CMinus.g:183:6: e1= expr '/=' e2= expr
					{
					pushFollow(FOLLOW_expr_in_assignStat1809);
					e1=expr();
					state._fsp--;
					if (state.failed) return retval;
					match(input,23,FOLLOW_23_in_assignStat1811); if (state.failed) return retval;
					pushFollow(FOLLOW_expr_in_assignStat1815);
					e2=expr();
					state._fsp--;
					if (state.failed) return retval;
					// TEMPLATE REWRITE
					if ( state.backtracking==0 ) {
					  // 183:27: -> assigndivide(lhs=$e1.strhs=$e2.st)
					  {
					  	retval.st = templateLib.getInstanceOf("assigndivide",new STAttrMap().put("lhs", (e1!=null?((StringTemplate)e1.getTemplate()):null)).put("rhs", (e2!=null?((StringTemplate)e2.getTemplate()):null)));
					  }


					}

					}
					break;

			}
			retval.stop = input.LT(-1);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "assignStat"


	public static class expr_return extends ParserRuleReturnScope {
		public StringTemplate st;
		public Object getTemplate() { return st; }
		public String toString() { return st==null?null:st.toString(); }
	};


	// $ANTLR start "expr"
	// CMinus.g:186:1: expr : ( condExpr -> {$condExpr.st}| aexpr -> {$aexpr.st}| arrayinit -> {$arrayinit.st});
	public final CMinusParser.expr_return expr() throws RecognitionException {
		CMinusParser.expr_return retval = new CMinusParser.expr_return();
		retval.start = input.LT(1);

		ParserRuleReturnScope condExpr40 =null;
		ParserRuleReturnScope aexpr41 =null;
		ParserRuleReturnScope arrayinit42 =null;

		try {
			// CMinus.g:186:5: ( condExpr -> {$condExpr.st}| aexpr -> {$aexpr.st}| arrayinit -> {$arrayinit.st})
			int alt16=3;
			switch ( input.LA(1) ) {
			case 59:
			case 82:
				{
				alt16=1;
				}
				break;
			case 11:
				{
				int LA16_3 = input.LA(2);
				if ( (synpred42_CMinus()) ) {
					alt16=1;
				}
				else if ( (synpred43_CMinus()) ) {
					alt16=2;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 16, 3, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case ID:
				{
				int LA16_4 = input.LA(2);
				if ( (synpred42_CMinus()) ) {
					alt16=1;
				}
				else if ( (synpred43_CMinus()) ) {
					alt16=2;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 16, 4, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case 61:
				{
				int LA16_5 = input.LA(2);
				if ( (synpred42_CMinus()) ) {
					alt16=1;
				}
				else if ( (synpred43_CMinus()) ) {
					alt16=2;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 16, 5, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case 76:
				{
				int LA16_6 = input.LA(2);
				if ( (synpred42_CMinus()) ) {
					alt16=1;
				}
				else if ( (synpred43_CMinus()) ) {
					alt16=2;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 16, 6, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case 51:
				{
				int LA16_7 = input.LA(2);
				if ( (synpred42_CMinus()) ) {
					alt16=1;
				}
				else if ( (synpred43_CMinus()) ) {
					alt16=2;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 16, 7, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case 77:
				{
				int LA16_8 = input.LA(2);
				if ( (synpred42_CMinus()) ) {
					alt16=1;
				}
				else if ( (synpred43_CMinus()) ) {
					alt16=2;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 16, 8, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case 53:
				{
				int LA16_9 = input.LA(2);
				if ( (synpred42_CMinus()) ) {
					alt16=1;
				}
				else if ( (synpred43_CMinus()) ) {
					alt16=2;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 16, 9, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case 54:
				{
				int LA16_10 = input.LA(2);
				if ( (synpred42_CMinus()) ) {
					alt16=1;
				}
				else if ( (synpred43_CMinus()) ) {
					alt16=2;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 16, 10, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case 57:
				{
				int LA16_11 = input.LA(2);
				if ( (synpred42_CMinus()) ) {
					alt16=1;
				}
				else if ( (synpred43_CMinus()) ) {
					alt16=2;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 16, 11, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case 80:
				{
				int LA16_12 = input.LA(2);
				if ( (synpred42_CMinus()) ) {
					alt16=1;
				}
				else if ( (synpred43_CMinus()) ) {
					alt16=2;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 16, 12, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case 81:
				{
				int LA16_13 = input.LA(2);
				if ( (synpred42_CMinus()) ) {
					alt16=1;
				}
				else if ( (synpred43_CMinus()) ) {
					alt16=2;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 16, 13, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case 78:
				{
				int LA16_14 = input.LA(2);
				if ( (synpred42_CMinus()) ) {
					alt16=1;
				}
				else if ( (synpred43_CMinus()) ) {
					alt16=2;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 16, 14, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case 70:
				{
				int LA16_15 = input.LA(2);
				if ( (synpred42_CMinus()) ) {
					alt16=1;
				}
				else if ( (synpred43_CMinus()) ) {
					alt16=2;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 16, 15, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case 71:
				{
				int LA16_16 = input.LA(2);
				if ( (synpred42_CMinus()) ) {
					alt16=1;
				}
				else if ( (synpred43_CMinus()) ) {
					alt16=2;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 16, 16, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case 79:
				{
				int LA16_17 = input.LA(2);
				if ( (synpred42_CMinus()) ) {
					alt16=1;
				}
				else if ( (synpred43_CMinus()) ) {
					alt16=2;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 16, 17, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case 74:
				{
				int LA16_18 = input.LA(2);
				if ( (synpred42_CMinus()) ) {
					alt16=1;
				}
				else if ( (synpred43_CMinus()) ) {
					alt16=2;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 16, 18, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case 50:
				{
				int LA16_19 = input.LA(2);
				if ( (synpred42_CMinus()) ) {
					alt16=1;
				}
				else if ( (synpred43_CMinus()) ) {
					alt16=2;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 16, 19, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case FP:
				{
				int LA16_20 = input.LA(2);
				if ( (synpred42_CMinus()) ) {
					alt16=1;
				}
				else if ( (synpred43_CMinus()) ) {
					alt16=2;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 16, 20, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case INT:
				{
				int LA16_21 = input.LA(2);
				if ( (synpred42_CMinus()) ) {
					alt16=1;
				}
				else if ( (synpred43_CMinus()) ) {
					alt16=2;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 16, 21, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case 19:
				{
				int LA16_22 = input.LA(2);
				if ( (synpred42_CMinus()) ) {
					alt16=1;
				}
				else if ( (synpred43_CMinus()) ) {
					alt16=2;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 16, 22, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case 89:
				{
				alt16=3;
				}
				break;
			default:
				if (state.backtracking>0) {state.failed=true; return retval;}
				NoViableAltException nvae =
					new NoViableAltException("", 16, 0, input);
				throw nvae;
			}
			switch (alt16) {
				case 1 :
					// CMinus.g:186:9: condExpr
					{
					pushFollow(FOLLOW_condExpr_in_expr1843);
					condExpr40=condExpr();
					state._fsp--;
					if (state.failed) return retval;
					// TEMPLATE REWRITE
					if ( state.backtracking==0 ) {
					  // 186:18: -> {$condExpr.st}
					  {
					  	retval.st = (condExpr40!=null?((StringTemplate)condExpr40.getTemplate()):null);
					  }


					}

					}
					break;
				case 2 :
					// CMinus.g:187:6: aexpr
					{
					pushFollow(FOLLOW_aexpr_in_expr1854);
					aexpr41=aexpr();
					state._fsp--;
					if (state.failed) return retval;
					// TEMPLATE REWRITE
					if ( state.backtracking==0 ) {
					  // 187:12: -> {$aexpr.st}
					  {
					  	retval.st = (aexpr41!=null?((StringTemplate)aexpr41.getTemplate()):null);
					  }


					}

					}
					break;
				case 3 :
					// CMinus.g:188:6: arrayinit
					{
					pushFollow(FOLLOW_arrayinit_in_expr1865);
					arrayinit42=arrayinit();
					state._fsp--;
					if (state.failed) return retval;
					// TEMPLATE REWRITE
					if ( state.backtracking==0 ) {
					  // 188:16: -> {$arrayinit.st}
					  {
					  	retval.st = (arrayinit42!=null?((StringTemplate)arrayinit42.getTemplate()):null);
					  }


					}

					}
					break;

			}
			retval.stop = input.LT(-1);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "expr"


	public static class arrayexpr_return extends ParserRuleReturnScope {
		public StringTemplate st;
		public Object getTemplate() { return st; }
		public String toString() { return st==null?null:st.toString(); }
	};


	// $ANTLR start "arrayexpr"
	// CMinus.g:191:1: arrayexpr : ID ( '[' p+= aexpr ']' )+ -> array(name=$ID.textindices=$p);
	public final CMinusParser.arrayexpr_return arrayexpr() throws RecognitionException {
		CMinusParser.arrayexpr_return retval = new CMinusParser.arrayexpr_return();
		retval.start = input.LT(1);

		Token ID43=null;
		List<Object> list_p=null;
		RuleReturnScope p = null;
		try {
			// CMinus.g:192:2: ( ID ( '[' p+= aexpr ']' )+ -> array(name=$ID.textindices=$p))
			// CMinus.g:192:6: ID ( '[' p+= aexpr ']' )+
			{
			ID43=(Token)match(input,ID,FOLLOW_ID_in_arrayexpr1886); if (state.failed) return retval;
			// CMinus.g:192:9: ( '[' p+= aexpr ']' )+
			int cnt17=0;
			loop17:
			while (true) {
				int alt17=2;
				int LA17_0 = input.LA(1);
				if ( (LA17_0==45) ) {
					alt17=1;
				}

				switch (alt17) {
				case 1 :
					// CMinus.g:192:10: '[' p+= aexpr ']'
					{
					match(input,45,FOLLOW_45_in_arrayexpr1889); if (state.failed) return retval;
					pushFollow(FOLLOW_aexpr_in_arrayexpr1893);
					p=aexpr();
					state._fsp--;
					if (state.failed) return retval;
					if (list_p==null) list_p=new ArrayList<Object>();
					list_p.add(p.getTemplate());
					match(input,48,FOLLOW_48_in_arrayexpr1895); if (state.failed) return retval;
					}
					break;

				default :
					if ( cnt17 >= 1 ) break loop17;
					if (state.backtracking>0) {state.failed=true; return retval;}
					EarlyExitException eee = new EarlyExitException(17, input);
					throw eee;
				}
				cnt17++;
			}

			// TEMPLATE REWRITE
			if ( state.backtracking==0 ) {
			  // 192:29: -> array(name=$ID.textindices=$p)
			  {
			  	retval.st = templateLib.getInstanceOf("array",new STAttrMap().put("name", (ID43!=null?ID43.getText():null)).put("indices", list_p));
			  }


			}

			}

			retval.stop = input.LT(-1);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "arrayexpr"


	public static class funcexpr_return extends ParserRuleReturnScope {
		public StringTemplate st;
		public Object getTemplate() { return st; }
		public String toString() { return st==null?null:st.toString(); }
	};


	// $ANTLR start "funcexpr"
	// CMinus.g:195:1: funcexpr : ( enumeratedFunction -> {$enumeratedFunction.st}| ID '(' (p+= aexpr ( ',' p+= aexpr )* )? ')' -> funcinstance(name=$ID.textargs=$p));
	public final CMinusParser.funcexpr_return funcexpr() throws RecognitionException {
		CMinusParser.funcexpr_return retval = new CMinusParser.funcexpr_return();
		retval.start = input.LT(1);

		Token ID45=null;
		List<Object> list_p=null;
		ParserRuleReturnScope enumeratedFunction44 =null;
		RuleReturnScope p = null;
		try {
			// CMinus.g:196:2: ( enumeratedFunction -> {$enumeratedFunction.st}| ID '(' (p+= aexpr ( ',' p+= aexpr )* )? ')' -> funcinstance(name=$ID.textargs=$p))
			int alt20=2;
			int LA20_0 = input.LA(1);
			if ( ((LA20_0 >= 50 && LA20_0 <= 51)||(LA20_0 >= 53 && LA20_0 <= 54)||LA20_0==57||LA20_0==61||(LA20_0 >= 70 && LA20_0 <= 71)||LA20_0==74||(LA20_0 >= 76 && LA20_0 <= 81)) ) {
				alt20=1;
			}
			else if ( (LA20_0==ID) ) {
				alt20=2;
			}

			else {
				if (state.backtracking>0) {state.failed=true; return retval;}
				NoViableAltException nvae =
					new NoViableAltException("", 20, 0, input);
				throw nvae;
			}

			switch (alt20) {
				case 1 :
					// CMinus.g:196:4: enumeratedFunction
					{
					pushFollow(FOLLOW_enumeratedFunction_in_funcexpr1922);
					enumeratedFunction44=enumeratedFunction();
					state._fsp--;
					if (state.failed) return retval;
					// TEMPLATE REWRITE
					if ( state.backtracking==0 ) {
					  // 196:23: -> {$enumeratedFunction.st}
					  {
					  	retval.st = (enumeratedFunction44!=null?((StringTemplate)enumeratedFunction44.getTemplate()):null);
					  }


					}

					}
					break;
				case 2 :
					// CMinus.g:197:9: ID '(' (p+= aexpr ( ',' p+= aexpr )* )? ')'
					{
					ID45=(Token)match(input,ID,FOLLOW_ID_in_funcexpr1936); if (state.failed) return retval;
					match(input,11,FOLLOW_11_in_funcexpr1938); if (state.failed) return retval;
					// CMinus.g:197:16: (p+= aexpr ( ',' p+= aexpr )* )?
					int alt19=2;
					int LA19_0 = input.LA(1);
					if ( ((LA19_0 >= FP && LA19_0 <= INT)||LA19_0==11||LA19_0==19||(LA19_0 >= 50 && LA19_0 <= 51)||(LA19_0 >= 53 && LA19_0 <= 54)||LA19_0==57||LA19_0==61||(LA19_0 >= 70 && LA19_0 <= 71)||LA19_0==74||(LA19_0 >= 76 && LA19_0 <= 81)) ) {
						alt19=1;
					}
					switch (alt19) {
						case 1 :
							// CMinus.g:197:18: p+= aexpr ( ',' p+= aexpr )*
							{
							pushFollow(FOLLOW_aexpr_in_funcexpr1944);
							p=aexpr();
							state._fsp--;
							if (state.failed) return retval;
							if (list_p==null) list_p=new ArrayList<Object>();
							list_p.add(p.getTemplate());
							// CMinus.g:197:27: ( ',' p+= aexpr )*
							loop18:
							while (true) {
								int alt18=2;
								int LA18_0 = input.LA(1);
								if ( (LA18_0==18) ) {
									alt18=1;
								}

								switch (alt18) {
								case 1 :
									// CMinus.g:197:29: ',' p+= aexpr
									{
									match(input,18,FOLLOW_18_in_funcexpr1948); if (state.failed) return retval;
									pushFollow(FOLLOW_aexpr_in_funcexpr1952);
									p=aexpr();
									state._fsp--;
									if (state.failed) return retval;
									if (list_p==null) list_p=new ArrayList<Object>();
									list_p.add(p.getTemplate());
									}
									break;

								default :
									break loop18;
								}
							}

							}
							break;

					}

					match(input,12,FOLLOW_12_in_funcexpr1960); if (state.failed) return retval;
					// TEMPLATE REWRITE
					if ( state.backtracking==0 ) {
					  // 197:52: -> funcinstance(name=$ID.textargs=$p)
					  {
					  	retval.st = templateLib.getInstanceOf("funcinstance",new STAttrMap().put("name", (ID45!=null?ID45.getText():null)).put("args", list_p));
					  }


					}

					}
					break;

			}
			retval.stop = input.LT(-1);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "funcexpr"


	public static class enumeratedFunction_return extends ParserRuleReturnScope {
		public StringTemplate st;
		public Object getTemplate() { return st; }
		public String toString() { return st==null?null:st.toString(); }
	};


	// $ANTLR start "enumeratedFunction"
	// CMinus.g:200:1: enumeratedFunction : ( 'floor' '(' aexpr ')' -> floor_function(argument=$aexpr.st)| 'round' '(' aexpr ')' -> round_function(argument=$aexpr.st)| 'ceil' '(' aexpr ')' -> ceil_function(argument=$aexpr.st)| 'sin' '(' aexpr ')' -> sin_function(argument=$aexpr.st)| 'cos' '(' aexpr ')' -> cos_function(argument=$aexpr.st)| 'cosh' '(' aexpr ')' -> cosh_function(argument=$aexpr.st)| 'exp' '(' aexpr ')' -> exp_function(argument=$aexpr.st)| 'tan' '(' aexpr ')' -> tan_function(argument=$aexpr.st)| 'tanh' '(' aexpr ')' -> tanh_function(argument=$aexpr.st)| 'sinh' '(' aexpr ')' -> sinh_function(argument=$aexpr.st)| 'log' '(' aexpr ')' -> log_function(argument=$aexpr.st)| 'log10' '(' aexpr ')' -> log10_function(argument=$aexpr.st)| 'sqrt' '(' aexpr ')' -> sqrt_function(argument=$aexpr.st)| 'pow' '(' base= aexpr ',' exponent= aexpr ')' -> pow_function(base=$base.stexponent=$exponent.st)| 'abs' '(' aexpr ')' -> abs_function(argument=$aexpr.st));
	public final CMinusParser.enumeratedFunction_return enumeratedFunction() throws RecognitionException {
		CMinusParser.enumeratedFunction_return retval = new CMinusParser.enumeratedFunction_return();
		retval.start = input.LT(1);

		ParserRuleReturnScope base =null;
		ParserRuleReturnScope exponent =null;
		ParserRuleReturnScope aexpr46 =null;
		ParserRuleReturnScope aexpr47 =null;
		ParserRuleReturnScope aexpr48 =null;
		ParserRuleReturnScope aexpr49 =null;
		ParserRuleReturnScope aexpr50 =null;
		ParserRuleReturnScope aexpr51 =null;
		ParserRuleReturnScope aexpr52 =null;
		ParserRuleReturnScope aexpr53 =null;
		ParserRuleReturnScope aexpr54 =null;
		ParserRuleReturnScope aexpr55 =null;
		ParserRuleReturnScope aexpr56 =null;
		ParserRuleReturnScope aexpr57 =null;
		ParserRuleReturnScope aexpr58 =null;
		ParserRuleReturnScope aexpr59 =null;

		try {
			// CMinus.g:201:2: ( 'floor' '(' aexpr ')' -> floor_function(argument=$aexpr.st)| 'round' '(' aexpr ')' -> round_function(argument=$aexpr.st)| 'ceil' '(' aexpr ')' -> ceil_function(argument=$aexpr.st)| 'sin' '(' aexpr ')' -> sin_function(argument=$aexpr.st)| 'cos' '(' aexpr ')' -> cos_function(argument=$aexpr.st)| 'cosh' '(' aexpr ')' -> cosh_function(argument=$aexpr.st)| 'exp' '(' aexpr ')' -> exp_function(argument=$aexpr.st)| 'tan' '(' aexpr ')' -> tan_function(argument=$aexpr.st)| 'tanh' '(' aexpr ')' -> tanh_function(argument=$aexpr.st)| 'sinh' '(' aexpr ')' -> sinh_function(argument=$aexpr.st)| 'log' '(' aexpr ')' -> log_function(argument=$aexpr.st)| 'log10' '(' aexpr ')' -> log10_function(argument=$aexpr.st)| 'sqrt' '(' aexpr ')' -> sqrt_function(argument=$aexpr.st)| 'pow' '(' base= aexpr ',' exponent= aexpr ')' -> pow_function(base=$base.stexponent=$exponent.st)| 'abs' '(' aexpr ')' -> abs_function(argument=$aexpr.st))
			int alt21=15;
			switch ( input.LA(1) ) {
			case 61:
				{
				alt21=1;
				}
				break;
			case 76:
				{
				alt21=2;
				}
				break;
			case 51:
				{
				alt21=3;
				}
				break;
			case 77:
				{
				alt21=4;
				}
				break;
			case 53:
				{
				alt21=5;
				}
				break;
			case 54:
				{
				alt21=6;
				}
				break;
			case 57:
				{
				alt21=7;
				}
				break;
			case 80:
				{
				alt21=8;
				}
				break;
			case 81:
				{
				alt21=9;
				}
				break;
			case 78:
				{
				alt21=10;
				}
				break;
			case 70:
				{
				alt21=11;
				}
				break;
			case 71:
				{
				alt21=12;
				}
				break;
			case 79:
				{
				alt21=13;
				}
				break;
			case 74:
				{
				alt21=14;
				}
				break;
			case 50:
				{
				alt21=15;
				}
				break;
			default:
				if (state.backtracking>0) {state.failed=true; return retval;}
				NoViableAltException nvae =
					new NoViableAltException("", 21, 0, input);
				throw nvae;
			}
			switch (alt21) {
				case 1 :
					// CMinus.g:201:4: 'floor' '(' aexpr ')'
					{
					match(input,61,FOLLOW_61_in_enumeratedFunction1985); if (state.failed) return retval;
					match(input,11,FOLLOW_11_in_enumeratedFunction1987); if (state.failed) return retval;
					pushFollow(FOLLOW_aexpr_in_enumeratedFunction1989);
					aexpr46=aexpr();
					state._fsp--;
					if (state.failed) return retval;
					match(input,12,FOLLOW_12_in_enumeratedFunction1991); if (state.failed) return retval;
					// TEMPLATE REWRITE
					if ( state.backtracking==0 ) {
					  // 201:26: -> floor_function(argument=$aexpr.st)
					  {
					  	retval.st = templateLib.getInstanceOf("floor_function",new STAttrMap().put("argument", (aexpr46!=null?((StringTemplate)aexpr46.getTemplate()):null)));
					  }


					}

					}
					break;
				case 2 :
					// CMinus.g:202:4: 'round' '(' aexpr ')'
					{
					match(input,76,FOLLOW_76_in_enumeratedFunction2005); if (state.failed) return retval;
					match(input,11,FOLLOW_11_in_enumeratedFunction2007); if (state.failed) return retval;
					pushFollow(FOLLOW_aexpr_in_enumeratedFunction2009);
					aexpr47=aexpr();
					state._fsp--;
					if (state.failed) return retval;
					match(input,12,FOLLOW_12_in_enumeratedFunction2011); if (state.failed) return retval;
					// TEMPLATE REWRITE
					if ( state.backtracking==0 ) {
					  // 202:26: -> round_function(argument=$aexpr.st)
					  {
					  	retval.st = templateLib.getInstanceOf("round_function",new STAttrMap().put("argument", (aexpr47!=null?((StringTemplate)aexpr47.getTemplate()):null)));
					  }


					}

					}
					break;
				case 3 :
					// CMinus.g:203:4: 'ceil' '(' aexpr ')'
					{
					match(input,51,FOLLOW_51_in_enumeratedFunction2025); if (state.failed) return retval;
					match(input,11,FOLLOW_11_in_enumeratedFunction2027); if (state.failed) return retval;
					pushFollow(FOLLOW_aexpr_in_enumeratedFunction2029);
					aexpr48=aexpr();
					state._fsp--;
					if (state.failed) return retval;
					match(input,12,FOLLOW_12_in_enumeratedFunction2031); if (state.failed) return retval;
					// TEMPLATE REWRITE
					if ( state.backtracking==0 ) {
					  // 203:25: -> ceil_function(argument=$aexpr.st)
					  {
					  	retval.st = templateLib.getInstanceOf("ceil_function",new STAttrMap().put("argument", (aexpr48!=null?((StringTemplate)aexpr48.getTemplate()):null)));
					  }


					}

					}
					break;
				case 4 :
					// CMinus.g:204:4: 'sin' '(' aexpr ')'
					{
					match(input,77,FOLLOW_77_in_enumeratedFunction2045); if (state.failed) return retval;
					match(input,11,FOLLOW_11_in_enumeratedFunction2047); if (state.failed) return retval;
					pushFollow(FOLLOW_aexpr_in_enumeratedFunction2049);
					aexpr49=aexpr();
					state._fsp--;
					if (state.failed) return retval;
					match(input,12,FOLLOW_12_in_enumeratedFunction2051); if (state.failed) return retval;
					// TEMPLATE REWRITE
					if ( state.backtracking==0 ) {
					  // 204:24: -> sin_function(argument=$aexpr.st)
					  {
					  	retval.st = templateLib.getInstanceOf("sin_function",new STAttrMap().put("argument", (aexpr49!=null?((StringTemplate)aexpr49.getTemplate()):null)));
					  }


					}

					}
					break;
				case 5 :
					// CMinus.g:205:4: 'cos' '(' aexpr ')'
					{
					match(input,53,FOLLOW_53_in_enumeratedFunction2065); if (state.failed) return retval;
					match(input,11,FOLLOW_11_in_enumeratedFunction2067); if (state.failed) return retval;
					pushFollow(FOLLOW_aexpr_in_enumeratedFunction2069);
					aexpr50=aexpr();
					state._fsp--;
					if (state.failed) return retval;
					match(input,12,FOLLOW_12_in_enumeratedFunction2071); if (state.failed) return retval;
					// TEMPLATE REWRITE
					if ( state.backtracking==0 ) {
					  // 205:24: -> cos_function(argument=$aexpr.st)
					  {
					  	retval.st = templateLib.getInstanceOf("cos_function",new STAttrMap().put("argument", (aexpr50!=null?((StringTemplate)aexpr50.getTemplate()):null)));
					  }


					}

					}
					break;
				case 6 :
					// CMinus.g:206:4: 'cosh' '(' aexpr ')'
					{
					match(input,54,FOLLOW_54_in_enumeratedFunction2085); if (state.failed) return retval;
					match(input,11,FOLLOW_11_in_enumeratedFunction2087); if (state.failed) return retval;
					pushFollow(FOLLOW_aexpr_in_enumeratedFunction2089);
					aexpr51=aexpr();
					state._fsp--;
					if (state.failed) return retval;
					match(input,12,FOLLOW_12_in_enumeratedFunction2091); if (state.failed) return retval;
					// TEMPLATE REWRITE
					if ( state.backtracking==0 ) {
					  // 206:25: -> cosh_function(argument=$aexpr.st)
					  {
					  	retval.st = templateLib.getInstanceOf("cosh_function",new STAttrMap().put("argument", (aexpr51!=null?((StringTemplate)aexpr51.getTemplate()):null)));
					  }


					}

					}
					break;
				case 7 :
					// CMinus.g:207:4: 'exp' '(' aexpr ')'
					{
					match(input,57,FOLLOW_57_in_enumeratedFunction2105); if (state.failed) return retval;
					match(input,11,FOLLOW_11_in_enumeratedFunction2107); if (state.failed) return retval;
					pushFollow(FOLLOW_aexpr_in_enumeratedFunction2109);
					aexpr52=aexpr();
					state._fsp--;
					if (state.failed) return retval;
					match(input,12,FOLLOW_12_in_enumeratedFunction2111); if (state.failed) return retval;
					// TEMPLATE REWRITE
					if ( state.backtracking==0 ) {
					  // 207:24: -> exp_function(argument=$aexpr.st)
					  {
					  	retval.st = templateLib.getInstanceOf("exp_function",new STAttrMap().put("argument", (aexpr52!=null?((StringTemplate)aexpr52.getTemplate()):null)));
					  }


					}

					}
					break;
				case 8 :
					// CMinus.g:208:4: 'tan' '(' aexpr ')'
					{
					match(input,80,FOLLOW_80_in_enumeratedFunction2125); if (state.failed) return retval;
					match(input,11,FOLLOW_11_in_enumeratedFunction2127); if (state.failed) return retval;
					pushFollow(FOLLOW_aexpr_in_enumeratedFunction2129);
					aexpr53=aexpr();
					state._fsp--;
					if (state.failed) return retval;
					match(input,12,FOLLOW_12_in_enumeratedFunction2131); if (state.failed) return retval;
					// TEMPLATE REWRITE
					if ( state.backtracking==0 ) {
					  // 208:24: -> tan_function(argument=$aexpr.st)
					  {
					  	retval.st = templateLib.getInstanceOf("tan_function",new STAttrMap().put("argument", (aexpr53!=null?((StringTemplate)aexpr53.getTemplate()):null)));
					  }


					}

					}
					break;
				case 9 :
					// CMinus.g:209:4: 'tanh' '(' aexpr ')'
					{
					match(input,81,FOLLOW_81_in_enumeratedFunction2145); if (state.failed) return retval;
					match(input,11,FOLLOW_11_in_enumeratedFunction2147); if (state.failed) return retval;
					pushFollow(FOLLOW_aexpr_in_enumeratedFunction2149);
					aexpr54=aexpr();
					state._fsp--;
					if (state.failed) return retval;
					match(input,12,FOLLOW_12_in_enumeratedFunction2151); if (state.failed) return retval;
					// TEMPLATE REWRITE
					if ( state.backtracking==0 ) {
					  // 209:25: -> tanh_function(argument=$aexpr.st)
					  {
					  	retval.st = templateLib.getInstanceOf("tanh_function",new STAttrMap().put("argument", (aexpr54!=null?((StringTemplate)aexpr54.getTemplate()):null)));
					  }


					}

					}
					break;
				case 10 :
					// CMinus.g:210:4: 'sinh' '(' aexpr ')'
					{
					match(input,78,FOLLOW_78_in_enumeratedFunction2165); if (state.failed) return retval;
					match(input,11,FOLLOW_11_in_enumeratedFunction2167); if (state.failed) return retval;
					pushFollow(FOLLOW_aexpr_in_enumeratedFunction2169);
					aexpr55=aexpr();
					state._fsp--;
					if (state.failed) return retval;
					match(input,12,FOLLOW_12_in_enumeratedFunction2171); if (state.failed) return retval;
					// TEMPLATE REWRITE
					if ( state.backtracking==0 ) {
					  // 210:25: -> sinh_function(argument=$aexpr.st)
					  {
					  	retval.st = templateLib.getInstanceOf("sinh_function",new STAttrMap().put("argument", (aexpr55!=null?((StringTemplate)aexpr55.getTemplate()):null)));
					  }


					}

					}
					break;
				case 11 :
					// CMinus.g:211:4: 'log' '(' aexpr ')'
					{
					match(input,70,FOLLOW_70_in_enumeratedFunction2185); if (state.failed) return retval;
					match(input,11,FOLLOW_11_in_enumeratedFunction2187); if (state.failed) return retval;
					pushFollow(FOLLOW_aexpr_in_enumeratedFunction2189);
					aexpr56=aexpr();
					state._fsp--;
					if (state.failed) return retval;
					match(input,12,FOLLOW_12_in_enumeratedFunction2191); if (state.failed) return retval;
					// TEMPLATE REWRITE
					if ( state.backtracking==0 ) {
					  // 211:24: -> log_function(argument=$aexpr.st)
					  {
					  	retval.st = templateLib.getInstanceOf("log_function",new STAttrMap().put("argument", (aexpr56!=null?((StringTemplate)aexpr56.getTemplate()):null)));
					  }


					}

					}
					break;
				case 12 :
					// CMinus.g:212:4: 'log10' '(' aexpr ')'
					{
					match(input,71,FOLLOW_71_in_enumeratedFunction2205); if (state.failed) return retval;
					match(input,11,FOLLOW_11_in_enumeratedFunction2207); if (state.failed) return retval;
					pushFollow(FOLLOW_aexpr_in_enumeratedFunction2209);
					aexpr57=aexpr();
					state._fsp--;
					if (state.failed) return retval;
					match(input,12,FOLLOW_12_in_enumeratedFunction2211); if (state.failed) return retval;
					// TEMPLATE REWRITE
					if ( state.backtracking==0 ) {
					  // 212:26: -> log10_function(argument=$aexpr.st)
					  {
					  	retval.st = templateLib.getInstanceOf("log10_function",new STAttrMap().put("argument", (aexpr57!=null?((StringTemplate)aexpr57.getTemplate()):null)));
					  }


					}

					}
					break;
				case 13 :
					// CMinus.g:213:4: 'sqrt' '(' aexpr ')'
					{
					match(input,79,FOLLOW_79_in_enumeratedFunction2225); if (state.failed) return retval;
					match(input,11,FOLLOW_11_in_enumeratedFunction2227); if (state.failed) return retval;
					pushFollow(FOLLOW_aexpr_in_enumeratedFunction2229);
					aexpr58=aexpr();
					state._fsp--;
					if (state.failed) return retval;
					match(input,12,FOLLOW_12_in_enumeratedFunction2231); if (state.failed) return retval;
					// TEMPLATE REWRITE
					if ( state.backtracking==0 ) {
					  // 213:25: -> sqrt_function(argument=$aexpr.st)
					  {
					  	retval.st = templateLib.getInstanceOf("sqrt_function",new STAttrMap().put("argument", (aexpr58!=null?((StringTemplate)aexpr58.getTemplate()):null)));
					  }


					}

					}
					break;
				case 14 :
					// CMinus.g:214:4: 'pow' '(' base= aexpr ',' exponent= aexpr ')'
					{
					match(input,74,FOLLOW_74_in_enumeratedFunction2245); if (state.failed) return retval;
					match(input,11,FOLLOW_11_in_enumeratedFunction2247); if (state.failed) return retval;
					pushFollow(FOLLOW_aexpr_in_enumeratedFunction2251);
					base=aexpr();
					state._fsp--;
					if (state.failed) return retval;
					match(input,18,FOLLOW_18_in_enumeratedFunction2253); if (state.failed) return retval;
					pushFollow(FOLLOW_aexpr_in_enumeratedFunction2257);
					exponent=aexpr();
					state._fsp--;
					if (state.failed) return retval;
					match(input,12,FOLLOW_12_in_enumeratedFunction2259); if (state.failed) return retval;
					// TEMPLATE REWRITE
					if ( state.backtracking==0 ) {
					  // 214:48: -> pow_function(base=$base.stexponent=$exponent.st)
					  {
					  	retval.st = templateLib.getInstanceOf("pow_function",new STAttrMap().put("base", (base!=null?((StringTemplate)base.getTemplate()):null)).put("exponent", (exponent!=null?((StringTemplate)exponent.getTemplate()):null)));
					  }


					}

					}
					break;
				case 15 :
					// CMinus.g:215:4: 'abs' '(' aexpr ')'
					{
					match(input,50,FOLLOW_50_in_enumeratedFunction2277); if (state.failed) return retval;
					match(input,11,FOLLOW_11_in_enumeratedFunction2279); if (state.failed) return retval;
					pushFollow(FOLLOW_aexpr_in_enumeratedFunction2281);
					aexpr59=aexpr();
					state._fsp--;
					if (state.failed) return retval;
					match(input,12,FOLLOW_12_in_enumeratedFunction2283); if (state.failed) return retval;
					// TEMPLATE REWRITE
					if ( state.backtracking==0 ) {
					  // 215:24: -> abs_function(argument=$aexpr.st)
					  {
					  	retval.st = templateLib.getInstanceOf("abs_function",new STAttrMap().put("argument", (aexpr59!=null?((StringTemplate)aexpr59.getTemplate()):null)));
					  }


					}

					}
					break;

			}
			retval.stop = input.LT(-1);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "enumeratedFunction"


	public static class constructExpr_return extends ParserRuleReturnScope {
		public StringTemplate st;
		public Object getTemplate() { return st; }
		public String toString() { return st==null?null:st.toString(); }
	};


	// $ANTLR start "constructExpr"
	// CMinus.g:218:1: constructExpr : id1= ID id2= ID '(' (p+= aexpr ( ',' p+= aexpr )* )? ')' -> constrinstance(type=$id1.textname=$id2.textargs=$p);
	public final CMinusParser.constructExpr_return constructExpr() throws RecognitionException {
		CMinusParser.constructExpr_return retval = new CMinusParser.constructExpr_return();
		retval.start = input.LT(1);

		Token id1=null;
		Token id2=null;
		List<Object> list_p=null;
		RuleReturnScope p = null;
		try {
			// CMinus.g:219:5: (id1= ID id2= ID '(' (p+= aexpr ( ',' p+= aexpr )* )? ')' -> constrinstance(type=$id1.textname=$id2.textargs=$p))
			// CMinus.g:219:9: id1= ID id2= ID '(' (p+= aexpr ( ',' p+= aexpr )* )? ')'
			{
			id1=(Token)match(input,ID,FOLLOW_ID_in_constructExpr2311); if (state.failed) return retval;
			id2=(Token)match(input,ID,FOLLOW_ID_in_constructExpr2315); if (state.failed) return retval;
			match(input,11,FOLLOW_11_in_constructExpr2317); if (state.failed) return retval;
			// CMinus.g:219:27: (p+= aexpr ( ',' p+= aexpr )* )?
			int alt23=2;
			int LA23_0 = input.LA(1);
			if ( ((LA23_0 >= FP && LA23_0 <= INT)||LA23_0==11||LA23_0==19||(LA23_0 >= 50 && LA23_0 <= 51)||(LA23_0 >= 53 && LA23_0 <= 54)||LA23_0==57||LA23_0==61||(LA23_0 >= 70 && LA23_0 <= 71)||LA23_0==74||(LA23_0 >= 76 && LA23_0 <= 81)) ) {
				alt23=1;
			}
			switch (alt23) {
				case 1 :
					// CMinus.g:219:29: p+= aexpr ( ',' p+= aexpr )*
					{
					pushFollow(FOLLOW_aexpr_in_constructExpr2323);
					p=aexpr();
					state._fsp--;
					if (state.failed) return retval;
					if (list_p==null) list_p=new ArrayList<Object>();
					list_p.add(p.getTemplate());
					// CMinus.g:219:38: ( ',' p+= aexpr )*
					loop22:
					while (true) {
						int alt22=2;
						int LA22_0 = input.LA(1);
						if ( (LA22_0==18) ) {
							alt22=1;
						}

						switch (alt22) {
						case 1 :
							// CMinus.g:219:40: ',' p+= aexpr
							{
							match(input,18,FOLLOW_18_in_constructExpr2327); if (state.failed) return retval;
							pushFollow(FOLLOW_aexpr_in_constructExpr2331);
							p=aexpr();
							state._fsp--;
							if (state.failed) return retval;
							if (list_p==null) list_p=new ArrayList<Object>();
							list_p.add(p.getTemplate());
							}
							break;

						default :
							break loop22;
						}
					}

					}
					break;

			}

			match(input,12,FOLLOW_12_in_constructExpr2339); if (state.failed) return retval;
			// TEMPLATE REWRITE
			if ( state.backtracking==0 ) {
			  // 219:63: -> constrinstance(type=$id1.textname=$id2.textargs=$p)
			  {
			  	retval.st = templateLib.getInstanceOf("constrinstance",new STAttrMap().put("type", (id1!=null?id1.getText():null)).put("name", (id2!=null?id2.getText():null)).put("args", list_p));
			  }


			}

			}

			retval.stop = input.LT(-1);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "constructExpr"


	public static class condExpr_return extends ParserRuleReturnScope {
		public StringTemplate st;
		public Object getTemplate() { return st; }
		public String toString() { return st==null?null:st.toString(); }
	};


	// $ANTLR start "condExpr"
	// CMinus.g:222:1: condExpr : (c1= condexp '&&' c2= condexp -> conjunction(left=$c1.stright=$c2.st)|c1= condexp '||' c2= condexp -> disjunction(left=$c1.stright=$c2.st)|c= condexp -> {$condexp.st});
	public final CMinusParser.condExpr_return condExpr() throws RecognitionException {
		CMinusParser.condExpr_return retval = new CMinusParser.condExpr_return();
		retval.start = input.LT(1);

		ParserRuleReturnScope c1 =null;
		ParserRuleReturnScope c2 =null;
		ParserRuleReturnScope c =null;

		try {
			// CMinus.g:223:2: (c1= condexp '&&' c2= condexp -> conjunction(left=$c1.stright=$c2.st)|c1= condexp '||' c2= condexp -> disjunction(left=$c1.stright=$c2.st)|c= condexp -> {$condexp.st})
			int alt24=3;
			switch ( input.LA(1) ) {
			case 82:
				{
				int LA24_1 = input.LA(2);
				if ( (synpred64_CMinus()) ) {
					alt24=1;
				}
				else if ( (synpred65_CMinus()) ) {
					alt24=2;
				}
				else if ( (true) ) {
					alt24=3;
				}

				}
				break;
			case 59:
				{
				int LA24_2 = input.LA(2);
				if ( (synpred64_CMinus()) ) {
					alt24=1;
				}
				else if ( (synpred65_CMinus()) ) {
					alt24=2;
				}
				else if ( (true) ) {
					alt24=3;
				}

				}
				break;
			case 11:
				{
				int LA24_3 = input.LA(2);
				if ( (synpred64_CMinus()) ) {
					alt24=1;
				}
				else if ( (synpred65_CMinus()) ) {
					alt24=2;
				}
				else if ( (true) ) {
					alt24=3;
				}

				}
				break;
			case ID:
				{
				int LA24_4 = input.LA(2);
				if ( (synpred64_CMinus()) ) {
					alt24=1;
				}
				else if ( (synpred65_CMinus()) ) {
					alt24=2;
				}
				else if ( (true) ) {
					alt24=3;
				}

				}
				break;
			case 61:
				{
				int LA24_5 = input.LA(2);
				if ( (synpred64_CMinus()) ) {
					alt24=1;
				}
				else if ( (synpred65_CMinus()) ) {
					alt24=2;
				}
				else if ( (true) ) {
					alt24=3;
				}

				}
				break;
			case 76:
				{
				int LA24_6 = input.LA(2);
				if ( (synpred64_CMinus()) ) {
					alt24=1;
				}
				else if ( (synpred65_CMinus()) ) {
					alt24=2;
				}
				else if ( (true) ) {
					alt24=3;
				}

				}
				break;
			case 51:
				{
				int LA24_7 = input.LA(2);
				if ( (synpred64_CMinus()) ) {
					alt24=1;
				}
				else if ( (synpred65_CMinus()) ) {
					alt24=2;
				}
				else if ( (true) ) {
					alt24=3;
				}

				}
				break;
			case 77:
				{
				int LA24_8 = input.LA(2);
				if ( (synpred64_CMinus()) ) {
					alt24=1;
				}
				else if ( (synpred65_CMinus()) ) {
					alt24=2;
				}
				else if ( (true) ) {
					alt24=3;
				}

				}
				break;
			case 53:
				{
				int LA24_9 = input.LA(2);
				if ( (synpred64_CMinus()) ) {
					alt24=1;
				}
				else if ( (synpred65_CMinus()) ) {
					alt24=2;
				}
				else if ( (true) ) {
					alt24=3;
				}

				}
				break;
			case 54:
				{
				int LA24_10 = input.LA(2);
				if ( (synpred64_CMinus()) ) {
					alt24=1;
				}
				else if ( (synpred65_CMinus()) ) {
					alt24=2;
				}
				else if ( (true) ) {
					alt24=3;
				}

				}
				break;
			case 57:
				{
				int LA24_11 = input.LA(2);
				if ( (synpred64_CMinus()) ) {
					alt24=1;
				}
				else if ( (synpred65_CMinus()) ) {
					alt24=2;
				}
				else if ( (true) ) {
					alt24=3;
				}

				}
				break;
			case 80:
				{
				int LA24_12 = input.LA(2);
				if ( (synpred64_CMinus()) ) {
					alt24=1;
				}
				else if ( (synpred65_CMinus()) ) {
					alt24=2;
				}
				else if ( (true) ) {
					alt24=3;
				}

				}
				break;
			case 81:
				{
				int LA24_13 = input.LA(2);
				if ( (synpred64_CMinus()) ) {
					alt24=1;
				}
				else if ( (synpred65_CMinus()) ) {
					alt24=2;
				}
				else if ( (true) ) {
					alt24=3;
				}

				}
				break;
			case 78:
				{
				int LA24_14 = input.LA(2);
				if ( (synpred64_CMinus()) ) {
					alt24=1;
				}
				else if ( (synpred65_CMinus()) ) {
					alt24=2;
				}
				else if ( (true) ) {
					alt24=3;
				}

				}
				break;
			case 70:
				{
				int LA24_15 = input.LA(2);
				if ( (synpred64_CMinus()) ) {
					alt24=1;
				}
				else if ( (synpred65_CMinus()) ) {
					alt24=2;
				}
				else if ( (true) ) {
					alt24=3;
				}

				}
				break;
			case 71:
				{
				int LA24_16 = input.LA(2);
				if ( (synpred64_CMinus()) ) {
					alt24=1;
				}
				else if ( (synpred65_CMinus()) ) {
					alt24=2;
				}
				else if ( (true) ) {
					alt24=3;
				}

				}
				break;
			case 79:
				{
				int LA24_17 = input.LA(2);
				if ( (synpred64_CMinus()) ) {
					alt24=1;
				}
				else if ( (synpred65_CMinus()) ) {
					alt24=2;
				}
				else if ( (true) ) {
					alt24=3;
				}

				}
				break;
			case 74:
				{
				int LA24_18 = input.LA(2);
				if ( (synpred64_CMinus()) ) {
					alt24=1;
				}
				else if ( (synpred65_CMinus()) ) {
					alt24=2;
				}
				else if ( (true) ) {
					alt24=3;
				}

				}
				break;
			case 50:
				{
				int LA24_19 = input.LA(2);
				if ( (synpred64_CMinus()) ) {
					alt24=1;
				}
				else if ( (synpred65_CMinus()) ) {
					alt24=2;
				}
				else if ( (true) ) {
					alt24=3;
				}

				}
				break;
			case FP:
				{
				int LA24_20 = input.LA(2);
				if ( (synpred64_CMinus()) ) {
					alt24=1;
				}
				else if ( (synpred65_CMinus()) ) {
					alt24=2;
				}
				else if ( (true) ) {
					alt24=3;
				}

				}
				break;
			case INT:
				{
				int LA24_21 = input.LA(2);
				if ( (synpred64_CMinus()) ) {
					alt24=1;
				}
				else if ( (synpred65_CMinus()) ) {
					alt24=2;
				}
				else if ( (true) ) {
					alt24=3;
				}

				}
				break;
			case 19:
				{
				int LA24_22 = input.LA(2);
				if ( (synpred64_CMinus()) ) {
					alt24=1;
				}
				else if ( (synpred65_CMinus()) ) {
					alt24=2;
				}
				else if ( (true) ) {
					alt24=3;
				}

				}
				break;
			default:
				if (state.backtracking>0) {state.failed=true; return retval;}
				NoViableAltException nvae =
					new NoViableAltException("", 24, 0, input);
				throw nvae;
			}
			switch (alt24) {
				case 1 :
					// CMinus.g:223:6: c1= condexp '&&' c2= condexp
					{
					pushFollow(FOLLOW_condexp_in_condExpr2372);
					c1=condexp();
					state._fsp--;
					if (state.failed) return retval;
					match(input,9,FOLLOW_9_in_condExpr2374); if (state.failed) return retval;
					pushFollow(FOLLOW_condexp_in_condExpr2378);
					c2=condexp();
					state._fsp--;
					if (state.failed) return retval;
					// TEMPLATE REWRITE
					if ( state.backtracking==0 ) {
					  // 223:33: -> conjunction(left=$c1.stright=$c2.st)
					  {
					  	retval.st = templateLib.getInstanceOf("conjunction",new STAttrMap().put("left", (c1!=null?((StringTemplate)c1.getTemplate()):null)).put("right", (c2!=null?((StringTemplate)c2.getTemplate()):null)));
					  }


					}

					}
					break;
				case 2 :
					// CMinus.g:224:6: c1= condexp '||' c2= condexp
					{
					pushFollow(FOLLOW_condexp_in_condExpr2400);
					c1=condexp();
					state._fsp--;
					if (state.failed) return retval;
					match(input,91,FOLLOW_91_in_condExpr2402); if (state.failed) return retval;
					pushFollow(FOLLOW_condexp_in_condExpr2406);
					c2=condexp();
					state._fsp--;
					if (state.failed) return retval;
					// TEMPLATE REWRITE
					if ( state.backtracking==0 ) {
					  // 224:33: -> disjunction(left=$c1.stright=$c2.st)
					  {
					  	retval.st = templateLib.getInstanceOf("disjunction",new STAttrMap().put("left", (c1!=null?((StringTemplate)c1.getTemplate()):null)).put("right", (c2!=null?((StringTemplate)c2.getTemplate()):null)));
					  }


					}

					}
					break;
				case 3 :
					// CMinus.g:225:6: c= condexp
					{
					pushFollow(FOLLOW_condexp_in_condExpr2428);
					c=condexp();
					state._fsp--;
					if (state.failed) return retval;
					// TEMPLATE REWRITE
					if ( state.backtracking==0 ) {
					  // 225:16: -> {$condexp.st}
					  {
					  	retval.st = (c!=null?((StringTemplate)c.getTemplate()):null);
					  }


					}

					}
					break;

			}
			retval.stop = input.LT(-1);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "condExpr"


	public static class condexp_return extends ParserRuleReturnScope {
		public StringTemplate st;
		public Object getTemplate() { return st; }
		public String toString() { return st==null?null:st.toString(); }
	};


	// $ANTLR start "condexp"
	// CMinus.g:228:1: condexp : ( 'true' -> boolean_true(| 'false' -> boolean_false(|a= aexpr ( ( '==' b= aexpr -> equals(left=$a.stright=$b.st)| '<' b= aexpr -> lessThan(left=$a.stright=$b.st)| '<=' b= aexpr -> lessOrEqual(left=$a.stright=$b.st)| '>=' b= aexpr -> moreOrEqual(left=$a.stright=$b.st)| '>' b= aexpr -> moreThan(left=$a.stright=$b.st)| '!=' b= aexpr -> unEqual(left=$a.stright=$b.st)) | -> {$a.st}) );
	public final CMinusParser.condexp_return condexp() throws RecognitionException {
		CMinusParser.condexp_return retval = new CMinusParser.condexp_return();
		retval.start = input.LT(1);

		ParserRuleReturnScope a =null;
		ParserRuleReturnScope b =null;

		try {
			// CMinus.g:229:2: ( 'true' -> boolean_true(| 'false' -> boolean_false(|a= aexpr ( ( '==' b= aexpr -> equals(left=$a.stright=$b.st)| '<' b= aexpr -> lessThan(left=$a.stright=$b.st)| '<=' b= aexpr -> lessOrEqual(left=$a.stright=$b.st)| '>=' b= aexpr -> moreOrEqual(left=$a.stright=$b.st)| '>' b= aexpr -> moreThan(left=$a.stright=$b.st)| '!=' b= aexpr -> unEqual(left=$a.stright=$b.st)) | -> {$a.st}) )
			int alt27=3;
			switch ( input.LA(1) ) {
			case 82:
				{
				alt27=1;
				}
				break;
			case 59:
				{
				alt27=2;
				}
				break;
			case FP:
			case ID:
			case INT:
			case 11:
			case 19:
			case 50:
			case 51:
			case 53:
			case 54:
			case 57:
			case 61:
			case 70:
			case 71:
			case 74:
			case 76:
			case 77:
			case 78:
			case 79:
			case 80:
			case 81:
				{
				alt27=3;
				}
				break;
			default:
				if (state.backtracking>0) {state.failed=true; return retval;}
				NoViableAltException nvae =
					new NoViableAltException("", 27, 0, input);
				throw nvae;
			}
			switch (alt27) {
				case 1 :
					// CMinus.g:229:4: 'true'
					{
					match(input,82,FOLLOW_82_in_condexp2443); if (state.failed) return retval;
					// TEMPLATE REWRITE
					if ( state.backtracking==0 ) {
					  // 229:11: -> boolean_true(
					  {
					  	retval.st = templateLib.getInstanceOf("boolean_true");
					  }


					}

					}
					break;
				case 2 :
					// CMinus.g:230:4: 'false'
					{
					match(input,59,FOLLOW_59_in_condexp2454); if (state.failed) return retval;
					// TEMPLATE REWRITE
					if ( state.backtracking==0 ) {
					  // 230:12: -> boolean_false(
					  {
					  	retval.st = templateLib.getInstanceOf("boolean_false");
					  }


					}

					}
					break;
				case 3 :
					// CMinus.g:231:9: a= aexpr ( ( '==' b= aexpr -> equals(left=$a.stright=$b.st)| '<' b= aexpr -> lessThan(left=$a.stright=$b.st)| '<=' b= aexpr -> lessOrEqual(left=$a.stright=$b.st)| '>=' b= aexpr -> moreOrEqual(left=$a.stright=$b.st)| '>' b= aexpr -> moreThan(left=$a.stright=$b.st)| '!=' b= aexpr -> unEqual(left=$a.stright=$b.st)) | -> {$a.st})
					{
					pushFollow(FOLLOW_aexpr_in_condexp2472);
					a=aexpr();
					state._fsp--;
					if (state.failed) return retval;
					// CMinus.g:232:9: ( ( '==' b= aexpr -> equals(left=$a.stright=$b.st)| '<' b= aexpr -> lessThan(left=$a.stright=$b.st)| '<=' b= aexpr -> lessOrEqual(left=$a.stright=$b.st)| '>=' b= aexpr -> moreOrEqual(left=$a.stright=$b.st)| '>' b= aexpr -> moreThan(left=$a.stright=$b.st)| '!=' b= aexpr -> unEqual(left=$a.stright=$b.st)) | -> {$a.st})
					int alt26=2;
					int LA26_0 = input.LA(1);
					if ( (LA26_0==8||LA26_0==25||LA26_0==27||(LA26_0 >= 29 && LA26_0 <= 31)) ) {
						alt26=1;
					}
					else if ( (LA26_0==EOF||LA26_0==9||LA26_0==12||LA26_0==14||(LA26_0 >= 16 && LA26_0 <= 17)||(LA26_0 >= 20 && LA26_0 <= 21)||(LA26_0 >= 23 && LA26_0 <= 24)||LA26_0==28||LA26_0==91) ) {
						alt26=2;
					}

					else {
						if (state.backtracking>0) {state.failed=true; return retval;}
						NoViableAltException nvae =
							new NoViableAltException("", 26, 0, input);
						throw nvae;
					}

					switch (alt26) {
						case 1 :
							// CMinus.g:232:13: ( '==' b= aexpr -> equals(left=$a.stright=$b.st)| '<' b= aexpr -> lessThan(left=$a.stright=$b.st)| '<=' b= aexpr -> lessOrEqual(left=$a.stright=$b.st)| '>=' b= aexpr -> moreOrEqual(left=$a.stright=$b.st)| '>' b= aexpr -> moreThan(left=$a.stright=$b.st)| '!=' b= aexpr -> unEqual(left=$a.stright=$b.st))
							{
							// CMinus.g:232:13: ( '==' b= aexpr -> equals(left=$a.stright=$b.st)| '<' b= aexpr -> lessThan(left=$a.stright=$b.st)| '<=' b= aexpr -> lessOrEqual(left=$a.stright=$b.st)| '>=' b= aexpr -> moreOrEqual(left=$a.stright=$b.st)| '>' b= aexpr -> moreThan(left=$a.stright=$b.st)| '!=' b= aexpr -> unEqual(left=$a.stright=$b.st))
							int alt25=6;
							switch ( input.LA(1) ) {
							case 29:
								{
								alt25=1;
								}
								break;
							case 25:
								{
								alt25=2;
								}
								break;
							case 27:
								{
								alt25=3;
								}
								break;
							case 31:
								{
								alt25=4;
								}
								break;
							case 30:
								{
								alt25=5;
								}
								break;
							case 8:
								{
								alt25=6;
								}
								break;
							default:
								if (state.backtracking>0) {state.failed=true; return retval;}
								NoViableAltException nvae =
									new NoViableAltException("", 25, 0, input);
								throw nvae;
							}
							switch (alt25) {
								case 1 :
									// CMinus.g:232:16: '==' b= aexpr
									{
									match(input,29,FOLLOW_29_in_condexp2489); if (state.failed) return retval;
									pushFollow(FOLLOW_aexpr_in_condexp2493);
									b=aexpr();
									state._fsp--;
									if (state.failed) return retval;
									// TEMPLATE REWRITE
									if ( state.backtracking==0 ) {
									  // 232:29: -> equals(left=$a.stright=$b.st)
									  {
									  	retval.st = templateLib.getInstanceOf("equals",new STAttrMap().put("left", (a!=null?((StringTemplate)a.getTemplate()):null)).put("right", (b!=null?((StringTemplate)b.getTemplate()):null)));
									  }


									}

									}
									break;
								case 2 :
									// CMinus.g:233:16: '<' b= aexpr
									{
									match(input,25,FOLLOW_25_in_condexp2523); if (state.failed) return retval;
									pushFollow(FOLLOW_aexpr_in_condexp2527);
									b=aexpr();
									state._fsp--;
									if (state.failed) return retval;
									// TEMPLATE REWRITE
									if ( state.backtracking==0 ) {
									  // 233:30: -> lessThan(left=$a.stright=$b.st)
									  {
									  	retval.st = templateLib.getInstanceOf("lessThan",new STAttrMap().put("left", (a!=null?((StringTemplate)a.getTemplate()):null)).put("right", (b!=null?((StringTemplate)b.getTemplate()):null)));
									  }


									}

									}
									break;
								case 3 :
									// CMinus.g:234:7: '<=' b= aexpr
									{
									match(input,27,FOLLOW_27_in_condexp2550); if (state.failed) return retval;
									pushFollow(FOLLOW_aexpr_in_condexp2554);
									b=aexpr();
									state._fsp--;
									if (state.failed) return retval;
									// TEMPLATE REWRITE
									if ( state.backtracking==0 ) {
									  // 234:22: -> lessOrEqual(left=$a.stright=$b.st)
									  {
									  	retval.st = templateLib.getInstanceOf("lessOrEqual",new STAttrMap().put("left", (a!=null?((StringTemplate)a.getTemplate()):null)).put("right", (b!=null?((StringTemplate)b.getTemplate()):null)));
									  }


									}

									}
									break;
								case 4 :
									// CMinus.g:235:7: '>=' b= aexpr
									{
									match(input,31,FOLLOW_31_in_condexp2577); if (state.failed) return retval;
									pushFollow(FOLLOW_aexpr_in_condexp2581);
									b=aexpr();
									state._fsp--;
									if (state.failed) return retval;
									// TEMPLATE REWRITE
									if ( state.backtracking==0 ) {
									  // 235:22: -> moreOrEqual(left=$a.stright=$b.st)
									  {
									  	retval.st = templateLib.getInstanceOf("moreOrEqual",new STAttrMap().put("left", (a!=null?((StringTemplate)a.getTemplate()):null)).put("right", (b!=null?((StringTemplate)b.getTemplate()):null)));
									  }


									}

									}
									break;
								case 5 :
									// CMinus.g:236:7: '>' b= aexpr
									{
									match(input,30,FOLLOW_30_in_condexp2604); if (state.failed) return retval;
									pushFollow(FOLLOW_aexpr_in_condexp2608);
									b=aexpr();
									state._fsp--;
									if (state.failed) return retval;
									// TEMPLATE REWRITE
									if ( state.backtracking==0 ) {
									  // 236:21: -> moreThan(left=$a.stright=$b.st)
									  {
									  	retval.st = templateLib.getInstanceOf("moreThan",new STAttrMap().put("left", (a!=null?((StringTemplate)a.getTemplate()):null)).put("right", (b!=null?((StringTemplate)b.getTemplate()):null)));
									  }


									}

									}
									break;
								case 6 :
									// CMinus.g:237:7: '!=' b= aexpr
									{
									match(input,8,FOLLOW_8_in_condexp2631); if (state.failed) return retval;
									pushFollow(FOLLOW_aexpr_in_condexp2635);
									b=aexpr();
									state._fsp--;
									if (state.failed) return retval;
									// TEMPLATE REWRITE
									if ( state.backtracking==0 ) {
									  // 237:22: -> unEqual(left=$a.stright=$b.st)
									  {
									  	retval.st = templateLib.getInstanceOf("unEqual",new STAttrMap().put("left", (a!=null?((StringTemplate)a.getTemplate()):null)).put("right", (b!=null?((StringTemplate)b.getTemplate()):null)));
									  }


									}

									}
									break;

							}

							}
							break;
						case 2 :
							// CMinus.g:239:13: 
							{
							// TEMPLATE REWRITE
							if ( state.backtracking==0 ) {
							  // 239:13: -> {$a.st}
							  {
							  	retval.st = (a!=null?((StringTemplate)a.getTemplate()):null);
							  }


							}

							}
							break;

					}

					}
					break;

			}
			retval.stop = input.LT(-1);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "condexp"


	public static class basicexpr_return extends ParserRuleReturnScope {
		public StringTemplate st;
		public Object getTemplate() { return st; }
		public String toString() { return st==null?null:st.toString(); }
	};


	// $ANTLR start "basicexpr"
	// CMinus.g:243:1: basicexpr : ( '(' type ')' atom -> castvalue(type=$type.stvalue=$atom.st)| atom -> {$atom.st});
	public final CMinusParser.basicexpr_return basicexpr() throws RecognitionException {
		CMinusParser.basicexpr_return retval = new CMinusParser.basicexpr_return();
		retval.start = input.LT(1);

		ParserRuleReturnScope type60 =null;
		ParserRuleReturnScope atom61 =null;
		ParserRuleReturnScope atom62 =null;

		try {
			// CMinus.g:244:2: ( '(' type ')' atom -> castvalue(type=$type.stvalue=$atom.st)| atom -> {$atom.st})
			int alt28=2;
			int LA28_0 = input.LA(1);
			if ( (LA28_0==11) ) {
				int LA28_1 = input.LA(2);
				if ( (synpred74_CMinus()) ) {
					alt28=1;
				}
				else if ( (true) ) {
					alt28=2;
				}

			}
			else if ( ((LA28_0 >= FP && LA28_0 <= INT)||LA28_0==19||(LA28_0 >= 50 && LA28_0 <= 51)||(LA28_0 >= 53 && LA28_0 <= 54)||LA28_0==57||LA28_0==61||(LA28_0 >= 70 && LA28_0 <= 71)||LA28_0==74||(LA28_0 >= 76 && LA28_0 <= 81)) ) {
				alt28=2;
			}

			else {
				if (state.backtracking>0) {state.failed=true; return retval;}
				NoViableAltException nvae =
					new NoViableAltException("", 28, 0, input);
				throw nvae;
			}

			switch (alt28) {
				case 1 :
					// CMinus.g:244:6: '(' type ')' atom
					{
					match(input,11,FOLLOW_11_in_basicexpr2708); if (state.failed) return retval;
					pushFollow(FOLLOW_type_in_basicexpr2710);
					type60=type();
					state._fsp--;
					if (state.failed) return retval;
					match(input,12,FOLLOW_12_in_basicexpr2712); if (state.failed) return retval;
					pushFollow(FOLLOW_atom_in_basicexpr2714);
					atom61=atom();
					state._fsp--;
					if (state.failed) return retval;
					// TEMPLATE REWRITE
					if ( state.backtracking==0 ) {
					  // 244:24: -> castvalue(type=$type.stvalue=$atom.st)
					  {
					  	retval.st = templateLib.getInstanceOf("castvalue",new STAttrMap().put("type", (type60!=null?((StringTemplate)type60.getTemplate()):null)).put("value", (atom61!=null?((StringTemplate)atom61.getTemplate()):null)));
					  }


					}

					}
					break;
				case 2 :
					// CMinus.g:245:6: atom
					{
					pushFollow(FOLLOW_atom_in_basicexpr2734);
					atom62=atom();
					state._fsp--;
					if (state.failed) return retval;
					// TEMPLATE REWRITE
					if ( state.backtracking==0 ) {
					  // 245:11: -> {$atom.st}
					  {
					  	retval.st = (atom62!=null?((StringTemplate)atom62.getTemplate()):null);
					  }


					}

					}
					break;

			}
			retval.stop = input.LT(-1);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "basicexpr"


	public static class aexpr_return extends ParserRuleReturnScope {
		public StringTemplate st;
		public Object getTemplate() { return st; }
		public String toString() { return st==null?null:st.toString(); }
	};


	// $ANTLR start "aexpr"
	// CMinus.g:248:1: aexpr : (a= basicexpr -> {$a.st}) ( ( '+' b= basicexpr -> add(left=$aexpr.stright=$b.st)) | ( '-' b= basicexpr -> substract(left=$aexpr.stright=$b.st)) | ( '*' b= basicexpr -> multiply(left=$aexpr.stright=$b.st)) | ( '/' b= basicexpr -> divide(left=$aexpr.stright=$b.st)) | ( '&' b= basicexpr -> bitwiseand(left=$aexpr.stright=$b.st)) | ( '|' b= basicexpr -> bitwiseor(left=$aexpr.stright=$b.st)) | ( '^' b= basicexpr -> bitwisexor(left=$aexpr.stright=$b.st)) | ( '<<' b= basicexpr -> bitwiseleftshift(left=$aexpr.stright=$b.st)) | ( '>>' b= basicexpr -> bitwiserightshift(left=$aexpr.stright=$b.st)) )* ;
	public final CMinusParser.aexpr_return aexpr() throws RecognitionException {
		CMinusParser.aexpr_return retval = new CMinusParser.aexpr_return();
		retval.start = input.LT(1);

		ParserRuleReturnScope a =null;
		ParserRuleReturnScope b =null;

		try {
			// CMinus.g:249:5: ( (a= basicexpr -> {$a.st}) ( ( '+' b= basicexpr -> add(left=$aexpr.stright=$b.st)) | ( '-' b= basicexpr -> substract(left=$aexpr.stright=$b.st)) | ( '*' b= basicexpr -> multiply(left=$aexpr.stright=$b.st)) | ( '/' b= basicexpr -> divide(left=$aexpr.stright=$b.st)) | ( '&' b= basicexpr -> bitwiseand(left=$aexpr.stright=$b.st)) | ( '|' b= basicexpr -> bitwiseor(left=$aexpr.stright=$b.st)) | ( '^' b= basicexpr -> bitwisexor(left=$aexpr.stright=$b.st)) | ( '<<' b= basicexpr -> bitwiseleftshift(left=$aexpr.stright=$b.st)) | ( '>>' b= basicexpr -> bitwiserightshift(left=$aexpr.stright=$b.st)) )* )
			// CMinus.g:249:9: (a= basicexpr -> {$a.st}) ( ( '+' b= basicexpr -> add(left=$aexpr.stright=$b.st)) | ( '-' b= basicexpr -> substract(left=$aexpr.stright=$b.st)) | ( '*' b= basicexpr -> multiply(left=$aexpr.stright=$b.st)) | ( '/' b= basicexpr -> divide(left=$aexpr.stright=$b.st)) | ( '&' b= basicexpr -> bitwiseand(left=$aexpr.stright=$b.st)) | ( '|' b= basicexpr -> bitwiseor(left=$aexpr.stright=$b.st)) | ( '^' b= basicexpr -> bitwisexor(left=$aexpr.stright=$b.st)) | ( '<<' b= basicexpr -> bitwiseleftshift(left=$aexpr.stright=$b.st)) | ( '>>' b= basicexpr -> bitwiserightshift(left=$aexpr.stright=$b.st)) )*
			{
			// CMinus.g:249:9: (a= basicexpr -> {$a.st})
			// CMinus.g:249:10: a= basicexpr
			{
			pushFollow(FOLLOW_basicexpr_in_aexpr2757);
			a=basicexpr();
			state._fsp--;
			if (state.failed) return retval;
			// TEMPLATE REWRITE
			if ( state.backtracking==0 ) {
			  // 249:22: -> {$a.st}
			  {
			  	retval.st = (a!=null?((StringTemplate)a.getTemplate()):null);
			  }


			}

			}

			// CMinus.g:250:9: ( ( '+' b= basicexpr -> add(left=$aexpr.stright=$b.st)) | ( '-' b= basicexpr -> substract(left=$aexpr.stright=$b.st)) | ( '*' b= basicexpr -> multiply(left=$aexpr.stright=$b.st)) | ( '/' b= basicexpr -> divide(left=$aexpr.stright=$b.st)) | ( '&' b= basicexpr -> bitwiseand(left=$aexpr.stright=$b.st)) | ( '|' b= basicexpr -> bitwiseor(left=$aexpr.stright=$b.st)) | ( '^' b= basicexpr -> bitwisexor(left=$aexpr.stright=$b.st)) | ( '<<' b= basicexpr -> bitwiseleftshift(left=$aexpr.stright=$b.st)) | ( '>>' b= basicexpr -> bitwiserightshift(left=$aexpr.stright=$b.st)) )*
			loop29:
			while (true) {
				int alt29=10;
				switch ( input.LA(1) ) {
				case 15:
					{
					alt29=1;
					}
					break;
				case 19:
					{
					alt29=2;
					}
					break;
				case 13:
					{
					alt29=3;
					}
					break;
				case 22:
					{
					alt29=4;
					}
					break;
				case 10:
					{
					alt29=5;
					}
					break;
				case 90:
					{
					alt29=6;
					}
					break;
				case 49:
					{
					alt29=7;
					}
					break;
				case 26:
					{
					alt29=8;
					}
					break;
				case 32:
					{
					alt29=9;
					}
					break;
				}
				switch (alt29) {
				case 1 :
					// CMinus.g:250:10: ( '+' b= basicexpr -> add(left=$aexpr.stright=$b.st))
					{
					// CMinus.g:250:10: ( '+' b= basicexpr -> add(left=$aexpr.stright=$b.st))
					// CMinus.g:250:12: '+' b= basicexpr
					{
					match(input,15,FOLLOW_15_in_aexpr2775); if (state.failed) return retval;
					pushFollow(FOLLOW_basicexpr_in_aexpr2779);
					b=basicexpr();
					state._fsp--;
					if (state.failed) return retval;
					// TEMPLATE REWRITE
					if ( state.backtracking==0 ) {
					  // 250:28: -> add(left=$aexpr.stright=$b.st)
					  {
					  	retval.st = templateLib.getInstanceOf("add",new STAttrMap().put("left", retval.st).put("right", (b!=null?((StringTemplate)b.getTemplate()):null)));
					  }


					}

					}

					}
					break;
				case 2 :
					// CMinus.g:251:3: ( '-' b= basicexpr -> substract(left=$aexpr.stright=$b.st))
					{
					// CMinus.g:251:3: ( '-' b= basicexpr -> substract(left=$aexpr.stright=$b.st))
					// CMinus.g:251:5: '-' b= basicexpr
					{
					match(input,19,FOLLOW_19_in_aexpr2803); if (state.failed) return retval;
					pushFollow(FOLLOW_basicexpr_in_aexpr2807);
					b=basicexpr();
					state._fsp--;
					if (state.failed) return retval;
					// TEMPLATE REWRITE
					if ( state.backtracking==0 ) {
					  // 251:21: -> substract(left=$aexpr.stright=$b.st)
					  {
					  	retval.st = templateLib.getInstanceOf("substract",new STAttrMap().put("left", retval.st).put("right", (b!=null?((StringTemplate)b.getTemplate()):null)));
					  }


					}

					}

					}
					break;
				case 3 :
					// CMinus.g:252:3: ( '*' b= basicexpr -> multiply(left=$aexpr.stright=$b.st))
					{
					// CMinus.g:252:3: ( '*' b= basicexpr -> multiply(left=$aexpr.stright=$b.st))
					// CMinus.g:252:5: '*' b= basicexpr
					{
					match(input,13,FOLLOW_13_in_aexpr2831); if (state.failed) return retval;
					pushFollow(FOLLOW_basicexpr_in_aexpr2835);
					b=basicexpr();
					state._fsp--;
					if (state.failed) return retval;
					// TEMPLATE REWRITE
					if ( state.backtracking==0 ) {
					  // 252:21: -> multiply(left=$aexpr.stright=$b.st)
					  {
					  	retval.st = templateLib.getInstanceOf("multiply",new STAttrMap().put("left", retval.st).put("right", (b!=null?((StringTemplate)b.getTemplate()):null)));
					  }


					}

					}

					}
					break;
				case 4 :
					// CMinus.g:253:3: ( '/' b= basicexpr -> divide(left=$aexpr.stright=$b.st))
					{
					// CMinus.g:253:3: ( '/' b= basicexpr -> divide(left=$aexpr.stright=$b.st))
					// CMinus.g:253:5: '/' b= basicexpr
					{
					match(input,22,FOLLOW_22_in_aexpr2859); if (state.failed) return retval;
					pushFollow(FOLLOW_basicexpr_in_aexpr2863);
					b=basicexpr();
					state._fsp--;
					if (state.failed) return retval;
					// TEMPLATE REWRITE
					if ( state.backtracking==0 ) {
					  // 253:21: -> divide(left=$aexpr.stright=$b.st)
					  {
					  	retval.st = templateLib.getInstanceOf("divide",new STAttrMap().put("left", retval.st).put("right", (b!=null?((StringTemplate)b.getTemplate()):null)));
					  }


					}

					}

					}
					break;
				case 5 :
					// CMinus.g:254:3: ( '&' b= basicexpr -> bitwiseand(left=$aexpr.stright=$b.st))
					{
					// CMinus.g:254:3: ( '&' b= basicexpr -> bitwiseand(left=$aexpr.stright=$b.st))
					// CMinus.g:254:5: '&' b= basicexpr
					{
					match(input,10,FOLLOW_10_in_aexpr2887); if (state.failed) return retval;
					pushFollow(FOLLOW_basicexpr_in_aexpr2891);
					b=basicexpr();
					state._fsp--;
					if (state.failed) return retval;
					// TEMPLATE REWRITE
					if ( state.backtracking==0 ) {
					  // 254:21: -> bitwiseand(left=$aexpr.stright=$b.st)
					  {
					  	retval.st = templateLib.getInstanceOf("bitwiseand",new STAttrMap().put("left", retval.st).put("right", (b!=null?((StringTemplate)b.getTemplate()):null)));
					  }


					}

					}

					}
					break;
				case 6 :
					// CMinus.g:255:3: ( '|' b= basicexpr -> bitwiseor(left=$aexpr.stright=$b.st))
					{
					// CMinus.g:255:3: ( '|' b= basicexpr -> bitwiseor(left=$aexpr.stright=$b.st))
					// CMinus.g:255:5: '|' b= basicexpr
					{
					match(input,90,FOLLOW_90_in_aexpr2915); if (state.failed) return retval;
					pushFollow(FOLLOW_basicexpr_in_aexpr2919);
					b=basicexpr();
					state._fsp--;
					if (state.failed) return retval;
					// TEMPLATE REWRITE
					if ( state.backtracking==0 ) {
					  // 255:21: -> bitwiseor(left=$aexpr.stright=$b.st)
					  {
					  	retval.st = templateLib.getInstanceOf("bitwiseor",new STAttrMap().put("left", retval.st).put("right", (b!=null?((StringTemplate)b.getTemplate()):null)));
					  }


					}

					}

					}
					break;
				case 7 :
					// CMinus.g:256:3: ( '^' b= basicexpr -> bitwisexor(left=$aexpr.stright=$b.st))
					{
					// CMinus.g:256:3: ( '^' b= basicexpr -> bitwisexor(left=$aexpr.stright=$b.st))
					// CMinus.g:256:5: '^' b= basicexpr
					{
					match(input,49,FOLLOW_49_in_aexpr2943); if (state.failed) return retval;
					pushFollow(FOLLOW_basicexpr_in_aexpr2947);
					b=basicexpr();
					state._fsp--;
					if (state.failed) return retval;
					// TEMPLATE REWRITE
					if ( state.backtracking==0 ) {
					  // 256:21: -> bitwisexor(left=$aexpr.stright=$b.st)
					  {
					  	retval.st = templateLib.getInstanceOf("bitwisexor",new STAttrMap().put("left", retval.st).put("right", (b!=null?((StringTemplate)b.getTemplate()):null)));
					  }


					}

					}

					}
					break;
				case 8 :
					// CMinus.g:257:3: ( '<<' b= basicexpr -> bitwiseleftshift(left=$aexpr.stright=$b.st))
					{
					// CMinus.g:257:3: ( '<<' b= basicexpr -> bitwiseleftshift(left=$aexpr.stright=$b.st))
					// CMinus.g:257:5: '<<' b= basicexpr
					{
					match(input,26,FOLLOW_26_in_aexpr2971); if (state.failed) return retval;
					pushFollow(FOLLOW_basicexpr_in_aexpr2975);
					b=basicexpr();
					state._fsp--;
					if (state.failed) return retval;
					// TEMPLATE REWRITE
					if ( state.backtracking==0 ) {
					  // 257:22: -> bitwiseleftshift(left=$aexpr.stright=$b.st)
					  {
					  	retval.st = templateLib.getInstanceOf("bitwiseleftshift",new STAttrMap().put("left", retval.st).put("right", (b!=null?((StringTemplate)b.getTemplate()):null)));
					  }


					}

					}

					}
					break;
				case 9 :
					// CMinus.g:258:3: ( '>>' b= basicexpr -> bitwiserightshift(left=$aexpr.stright=$b.st))
					{
					// CMinus.g:258:3: ( '>>' b= basicexpr -> bitwiserightshift(left=$aexpr.stright=$b.st))
					// CMinus.g:258:5: '>>' b= basicexpr
					{
					match(input,32,FOLLOW_32_in_aexpr2999); if (state.failed) return retval;
					pushFollow(FOLLOW_basicexpr_in_aexpr3003);
					b=basicexpr();
					state._fsp--;
					if (state.failed) return retval;
					// TEMPLATE REWRITE
					if ( state.backtracking==0 ) {
					  // 258:22: -> bitwiserightshift(left=$aexpr.stright=$b.st)
					  {
					  	retval.st = templateLib.getInstanceOf("bitwiserightshift",new STAttrMap().put("left", retval.st).put("right", (b!=null?((StringTemplate)b.getTemplate()):null)));
					  }


					}

					}

					}
					break;

				default :
					break loop29;
				}
			}

			}

			retval.stop = input.LT(-1);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "aexpr"


	public static class atom_return extends ParserRuleReturnScope {
		public StringTemplate st;
		public Object getTemplate() { return st; }
		public String toString() { return st==null?null:st.toString(); }
	};


	// $ANTLR start "atom"
	// CMinus.g:261:1: atom : ( arrayexpr -> {$arrayexpr.st}| funcexpr -> {$funcexpr.st}| ID -> refVar(id=$ID.text)| numericexpr -> iconst(value=$numericexpr.st)| '(' expr ')' -> brackets(expr=$expr.st)| '-' a= atom -> minusNumber(value=$a.st));
	public final CMinusParser.atom_return atom() throws RecognitionException {
		CMinusParser.atom_return retval = new CMinusParser.atom_return();
		retval.start = input.LT(1);

		Token ID65=null;
		ParserRuleReturnScope a =null;
		ParserRuleReturnScope arrayexpr63 =null;
		ParserRuleReturnScope funcexpr64 =null;
		ParserRuleReturnScope numericexpr66 =null;
		ParserRuleReturnScope expr67 =null;

		try {
			// CMinus.g:262:5: ( arrayexpr -> {$arrayexpr.st}| funcexpr -> {$funcexpr.st}| ID -> refVar(id=$ID.text)| numericexpr -> iconst(value=$numericexpr.st)| '(' expr ')' -> brackets(expr=$expr.st)| '-' a= atom -> minusNumber(value=$a.st))
			int alt30=6;
			switch ( input.LA(1) ) {
			case ID:
				{
				switch ( input.LA(2) ) {
				case 11:
					{
					alt30=2;
					}
					break;
				case 45:
					{
					alt30=1;
					}
					break;
				case EOF:
				case 8:
				case 9:
				case 10:
				case 12:
				case 13:
				case 14:
				case 15:
				case 16:
				case 17:
				case 18:
				case 19:
				case 20:
				case 21:
				case 22:
				case 23:
				case 24:
				case 25:
				case 26:
				case 27:
				case 28:
				case 29:
				case 30:
				case 31:
				case 32:
				case 48:
				case 49:
				case 90:
				case 91:
					{
					alt30=3;
					}
					break;
				default:
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 30, 1, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}
				}
				break;
			case 50:
			case 51:
			case 53:
			case 54:
			case 57:
			case 61:
			case 70:
			case 71:
			case 74:
			case 76:
			case 77:
			case 78:
			case 79:
			case 80:
			case 81:
				{
				alt30=2;
				}
				break;
			case FP:
			case INT:
				{
				alt30=4;
				}
				break;
			case 11:
				{
				alt30=5;
				}
				break;
			case 19:
				{
				alt30=6;
				}
				break;
			default:
				if (state.backtracking>0) {state.failed=true; return retval;}
				NoViableAltException nvae =
					new NoViableAltException("", 30, 0, input);
				throw nvae;
			}
			switch (alt30) {
				case 1 :
					// CMinus.g:262:7: arrayexpr
					{
					pushFollow(FOLLOW_arrayexpr_in_atom3038);
					arrayexpr63=arrayexpr();
					state._fsp--;
					if (state.failed) return retval;
					// TEMPLATE REWRITE
					if ( state.backtracking==0 ) {
					  // 262:17: -> {$arrayexpr.st}
					  {
					  	retval.st = (arrayexpr63!=null?((StringTemplate)arrayexpr63.getTemplate()):null);
					  }


					}

					}
					break;
				case 2 :
					// CMinus.g:263:4: funcexpr
					{
					pushFollow(FOLLOW_funcexpr_in_atom3047);
					funcexpr64=funcexpr();
					state._fsp--;
					if (state.failed) return retval;
					// TEMPLATE REWRITE
					if ( state.backtracking==0 ) {
					  // 263:13: -> {$funcexpr.st}
					  {
					  	retval.st = (funcexpr64!=null?((StringTemplate)funcexpr64.getTemplate()):null);
					  }


					}

					}
					break;
				case 3 :
					// CMinus.g:264:7: ID
					{
					ID65=(Token)match(input,ID,FOLLOW_ID_in_atom3059); if (state.failed) return retval;
					// TEMPLATE REWRITE
					if ( state.backtracking==0 ) {
					  // 264:10: -> refVar(id=$ID.text)
					  {
					  	retval.st = templateLib.getInstanceOf("refVar",new STAttrMap().put("id", (ID65!=null?ID65.getText():null)));
					  }


					}

					}
					break;
				case 4 :
					// CMinus.g:265:7: numericexpr
					{
					pushFollow(FOLLOW_numericexpr_in_atom3076);
					numericexpr66=numericexpr();
					state._fsp--;
					if (state.failed) return retval;
					// TEMPLATE REWRITE
					if ( state.backtracking==0 ) {
					  // 265:19: -> iconst(value=$numericexpr.st)
					  {
					  	retval.st = templateLib.getInstanceOf("iconst",new STAttrMap().put("value", (numericexpr66!=null?((StringTemplate)numericexpr66.getTemplate()):null)));
					  }


					}

					}
					break;
				case 5 :
					// CMinus.g:266:7: '(' expr ')'
					{
					match(input,11,FOLLOW_11_in_atom3093); if (state.failed) return retval;
					pushFollow(FOLLOW_expr_in_atom3095);
					expr67=expr();
					state._fsp--;
					if (state.failed) return retval;
					match(input,12,FOLLOW_12_in_atom3097); if (state.failed) return retval;
					// TEMPLATE REWRITE
					if ( state.backtracking==0 ) {
					  // 266:20: -> brackets(expr=$expr.st)
					  {
					  	retval.st = templateLib.getInstanceOf("brackets",new STAttrMap().put("expr", (expr67!=null?((StringTemplate)expr67.getTemplate()):null)));
					  }


					}

					}
					break;
				case 6 :
					// CMinus.g:267:4: '-' a= atom
					{
					match(input,19,FOLLOW_19_in_atom3111); if (state.failed) return retval;
					pushFollow(FOLLOW_atom_in_atom3115);
					a=atom();
					state._fsp--;
					if (state.failed) return retval;
					// TEMPLATE REWRITE
					if ( state.backtracking==0 ) {
					  // 267:15: -> minusNumber(value=$a.st)
					  {
					  	retval.st = templateLib.getInstanceOf("minusNumber",new STAttrMap().put("value", (a!=null?((StringTemplate)a.getTemplate()):null)));
					  }


					}

					}
					break;

			}
			retval.stop = input.LT(-1);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "atom"


	public static class realnum_return extends ParserRuleReturnScope {
		public StringTemplate st;
		public Object getTemplate() { return st; }
		public String toString() { return st==null?null:st.toString(); }
	};


	// $ANTLR start "realnum"
	// CMinus.g:270:1: realnum : ( numericexpr -> {$numericexpr.st}| '-' numericexpr -> minusNumber(value=$numericexpr.st));
	public final CMinusParser.realnum_return realnum() throws RecognitionException {
		CMinusParser.realnum_return retval = new CMinusParser.realnum_return();
		retval.start = input.LT(1);

		ParserRuleReturnScope numericexpr68 =null;
		ParserRuleReturnScope numericexpr69 =null;

		try {
			// CMinus.g:271:5: ( numericexpr -> {$numericexpr.st}| '-' numericexpr -> minusNumber(value=$numericexpr.st))
			int alt31=2;
			int LA31_0 = input.LA(1);
			if ( (LA31_0==FP||LA31_0==INT) ) {
				alt31=1;
			}
			else if ( (LA31_0==19) ) {
				alt31=2;
			}

			else {
				if (state.backtracking>0) {state.failed=true; return retval;}
				NoViableAltException nvae =
					new NoViableAltException("", 31, 0, input);
				throw nvae;
			}

			switch (alt31) {
				case 1 :
					// CMinus.g:271:7: numericexpr
					{
					pushFollow(FOLLOW_numericexpr_in_realnum3143);
					numericexpr68=numericexpr();
					state._fsp--;
					if (state.failed) return retval;
					// TEMPLATE REWRITE
					if ( state.backtracking==0 ) {
					  // 271:19: -> {$numericexpr.st}
					  {
					  	retval.st = (numericexpr68!=null?((StringTemplate)numericexpr68.getTemplate()):null);
					  }


					}

					}
					break;
				case 2 :
					// CMinus.g:272:4: '-' numericexpr
					{
					match(input,19,FOLLOW_19_in_realnum3152); if (state.failed) return retval;
					pushFollow(FOLLOW_numericexpr_in_realnum3154);
					numericexpr69=numericexpr();
					state._fsp--;
					if (state.failed) return retval;
					// TEMPLATE REWRITE
					if ( state.backtracking==0 ) {
					  // 272:20: -> minusNumber(value=$numericexpr.st)
					  {
					  	retval.st = templateLib.getInstanceOf("minusNumber",new STAttrMap().put("value", (numericexpr69!=null?((StringTemplate)numericexpr69.getTemplate()):null)));
					  }


					}

					}
					break;

			}
			retval.stop = input.LT(-1);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "realnum"


	public static class numericexpr_return extends ParserRuleReturnScope {
		public StringTemplate st;
		public Object getTemplate() { return st; }
		public String toString() { return st==null?null:st.toString(); }
	};


	// $ANTLR start "numericexpr"
	// CMinus.g:275:1: numericexpr : ( FP ( 'f' | 'F' ) -> floatFloatingPoint(value=$FP.text)| FP -> doubleFloatingPoint(value=$FP.text)| INT ( 'u' | 'U' ) -> unsignedInt(value=$INT.text)| INT ( 'l' | 'L' ) -> longInt(value=$INT.text)| INT ( 'll' | 'LL' ) -> longLongInt(value=$INT.text)| INT ( 'lu' | 'Lu' | 'lU' | 'LU' | 'ul' | 'uL' | 'Ul' | 'UL' ) -> unsignedLongInt(value=$INT.text)| INT ( 'llu' | 'LLu' | 'llU' | 'LLU' | 'ull' | 'uLL' | 'Ull' | 'ULL' ) -> unsignedLongLongInt(value=$INT.text)| INT -> defaultInt(value=$INT.text));
	public final CMinusParser.numericexpr_return numericexpr() throws RecognitionException {
		CMinusParser.numericexpr_return retval = new CMinusParser.numericexpr_return();
		retval.start = input.LT(1);

		Token FP70=null;
		Token FP71=null;
		Token INT72=null;
		Token INT73=null;
		Token INT74=null;
		Token INT75=null;
		Token INT76=null;
		Token INT77=null;

		try {
			// CMinus.g:276:5: ( FP ( 'f' | 'F' ) -> floatFloatingPoint(value=$FP.text)| FP -> doubleFloatingPoint(value=$FP.text)| INT ( 'u' | 'U' ) -> unsignedInt(value=$INT.text)| INT ( 'l' | 'L' ) -> longInt(value=$INT.text)| INT ( 'll' | 'LL' ) -> longLongInt(value=$INT.text)| INT ( 'lu' | 'Lu' | 'lU' | 'LU' | 'ul' | 'uL' | 'Ul' | 'UL' ) -> unsignedLongInt(value=$INT.text)| INT ( 'llu' | 'LLu' | 'llU' | 'LLU' | 'ull' | 'uLL' | 'Ull' | 'ULL' ) -> unsignedLongLongInt(value=$INT.text)| INT -> defaultInt(value=$INT.text))
			int alt38=8;
			int LA38_0 = input.LA(1);
			if ( (LA38_0==FP) ) {
				int LA38_1 = input.LA(2);
				if ( (LA38_1==33||LA38_1==58) ) {
					alt38=1;
				}
				else if ( (LA38_1==EOF||(LA38_1 >= 8 && LA38_1 <= 10)||(LA38_1 >= 12 && LA38_1 <= 32)||(LA38_1 >= 48 && LA38_1 <= 49)||(LA38_1 >= 90 && LA38_1 <= 92)) ) {
					alt38=2;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 38, 1, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

			}
			else if ( (LA38_0==INT) ) {
				switch ( input.LA(2) ) {
				case 40:
				case 83:
					{
					alt38=3;
					}
					break;
				case 34:
				case 65:
					{
					alt38=4;
					}
					break;
				case 35:
				case 67:
					{
					alt38=5;
					}
					break;
				case 38:
				case 39:
				case 41:
				case 43:
				case 66:
				case 73:
				case 84:
				case 86:
					{
					alt38=6;
					}
					break;
				case 36:
				case 37:
				case 42:
				case 44:
				case 68:
				case 69:
				case 85:
				case 87:
					{
					alt38=7;
					}
					break;
				case EOF:
				case 8:
				case 9:
				case 10:
				case 12:
				case 13:
				case 14:
				case 15:
				case 16:
				case 17:
				case 18:
				case 19:
				case 20:
				case 21:
				case 22:
				case 23:
				case 24:
				case 25:
				case 26:
				case 27:
				case 28:
				case 29:
				case 30:
				case 31:
				case 32:
				case 48:
				case 49:
				case 90:
				case 91:
				case 92:
					{
					alt38=8;
					}
					break;
				default:
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 38, 2, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}
			}

			else {
				if (state.backtracking>0) {state.failed=true; return retval;}
				NoViableAltException nvae =
					new NoViableAltException("", 38, 0, input);
				throw nvae;
			}

			switch (alt38) {
				case 1 :
					// CMinus.g:276:7: FP ( 'f' | 'F' )
					{
					FP70=(Token)match(input,FP,FOLLOW_FP_in_numericexpr3178); if (state.failed) return retval;
					// CMinus.g:276:10: ( 'f' | 'F' )
					int alt32=2;
					int LA32_0 = input.LA(1);
					if ( (LA32_0==58) ) {
						alt32=1;
					}
					else if ( (LA32_0==33) ) {
						alt32=2;
					}

					else {
						if (state.backtracking>0) {state.failed=true; return retval;}
						NoViableAltException nvae =
							new NoViableAltException("", 32, 0, input);
						throw nvae;
					}

					switch (alt32) {
						case 1 :
							// CMinus.g:276:11: 'f'
							{
							match(input,58,FOLLOW_58_in_numericexpr3181); if (state.failed) return retval;
							}
							break;
						case 2 :
							// CMinus.g:276:15: 'F'
							{
							match(input,33,FOLLOW_33_in_numericexpr3183); if (state.failed) return retval;
							}
							break;

					}

					// TEMPLATE REWRITE
					if ( state.backtracking==0 ) {
					  // 276:20: -> floatFloatingPoint(value=$FP.text)
					  {
					  	retval.st = templateLib.getInstanceOf("floatFloatingPoint",new STAttrMap().put("value", (FP70!=null?FP70.getText():null)));
					  }


					}

					}
					break;
				case 2 :
					// CMinus.g:277:4: FP
					{
					FP71=(Token)match(input,FP,FOLLOW_FP_in_numericexpr3198); if (state.failed) return retval;
					// TEMPLATE REWRITE
					if ( state.backtracking==0 ) {
					  // 277:7: -> doubleFloatingPoint(value=$FP.text)
					  {
					  	retval.st = templateLib.getInstanceOf("doubleFloatingPoint",new STAttrMap().put("value", (FP71!=null?FP71.getText():null)));
					  }


					}

					}
					break;
				case 3 :
					// CMinus.g:278:4: INT ( 'u' | 'U' )
					{
					INT72=(Token)match(input,INT,FOLLOW_INT_in_numericexpr3212); if (state.failed) return retval;
					// CMinus.g:278:8: ( 'u' | 'U' )
					int alt33=2;
					int LA33_0 = input.LA(1);
					if ( (LA33_0==83) ) {
						alt33=1;
					}
					else if ( (LA33_0==40) ) {
						alt33=2;
					}

					else {
						if (state.backtracking>0) {state.failed=true; return retval;}
						NoViableAltException nvae =
							new NoViableAltException("", 33, 0, input);
						throw nvae;
					}

					switch (alt33) {
						case 1 :
							// CMinus.g:278:9: 'u'
							{
							match(input,83,FOLLOW_83_in_numericexpr3215); if (state.failed) return retval;
							}
							break;
						case 2 :
							// CMinus.g:278:13: 'U'
							{
							match(input,40,FOLLOW_40_in_numericexpr3217); if (state.failed) return retval;
							}
							break;

					}

					// TEMPLATE REWRITE
					if ( state.backtracking==0 ) {
					  // 278:18: -> unsignedInt(value=$INT.text)
					  {
					  	retval.st = templateLib.getInstanceOf("unsignedInt",new STAttrMap().put("value", (INT72!=null?INT72.getText():null)));
					  }


					}

					}
					break;
				case 4 :
					// CMinus.g:279:4: INT ( 'l' | 'L' )
					{
					INT73=(Token)match(input,INT,FOLLOW_INT_in_numericexpr3232); if (state.failed) return retval;
					// CMinus.g:279:8: ( 'l' | 'L' )
					int alt34=2;
					int LA34_0 = input.LA(1);
					if ( (LA34_0==65) ) {
						alt34=1;
					}
					else if ( (LA34_0==34) ) {
						alt34=2;
					}

					else {
						if (state.backtracking>0) {state.failed=true; return retval;}
						NoViableAltException nvae =
							new NoViableAltException("", 34, 0, input);
						throw nvae;
					}

					switch (alt34) {
						case 1 :
							// CMinus.g:279:9: 'l'
							{
							match(input,65,FOLLOW_65_in_numericexpr3235); if (state.failed) return retval;
							}
							break;
						case 2 :
							// CMinus.g:279:13: 'L'
							{
							match(input,34,FOLLOW_34_in_numericexpr3237); if (state.failed) return retval;
							}
							break;

					}

					// TEMPLATE REWRITE
					if ( state.backtracking==0 ) {
					  // 279:18: -> longInt(value=$INT.text)
					  {
					  	retval.st = templateLib.getInstanceOf("longInt",new STAttrMap().put("value", (INT73!=null?INT73.getText():null)));
					  }


					}

					}
					break;
				case 5 :
					// CMinus.g:280:4: INT ( 'll' | 'LL' )
					{
					INT74=(Token)match(input,INT,FOLLOW_INT_in_numericexpr3252); if (state.failed) return retval;
					// CMinus.g:280:8: ( 'll' | 'LL' )
					int alt35=2;
					int LA35_0 = input.LA(1);
					if ( (LA35_0==67) ) {
						alt35=1;
					}
					else if ( (LA35_0==35) ) {
						alt35=2;
					}

					else {
						if (state.backtracking>0) {state.failed=true; return retval;}
						NoViableAltException nvae =
							new NoViableAltException("", 35, 0, input);
						throw nvae;
					}

					switch (alt35) {
						case 1 :
							// CMinus.g:280:9: 'll'
							{
							match(input,67,FOLLOW_67_in_numericexpr3255); if (state.failed) return retval;
							}
							break;
						case 2 :
							// CMinus.g:280:14: 'LL'
							{
							match(input,35,FOLLOW_35_in_numericexpr3257); if (state.failed) return retval;
							}
							break;

					}

					// TEMPLATE REWRITE
					if ( state.backtracking==0 ) {
					  // 280:20: -> longLongInt(value=$INT.text)
					  {
					  	retval.st = templateLib.getInstanceOf("longLongInt",new STAttrMap().put("value", (INT74!=null?INT74.getText():null)));
					  }


					}

					}
					break;
				case 6 :
					// CMinus.g:281:4: INT ( 'lu' | 'Lu' | 'lU' | 'LU' | 'ul' | 'uL' | 'Ul' | 'UL' )
					{
					INT75=(Token)match(input,INT,FOLLOW_INT_in_numericexpr3272); if (state.failed) return retval;
					// CMinus.g:281:8: ( 'lu' | 'Lu' | 'lU' | 'LU' | 'ul' | 'uL' | 'Ul' | 'UL' )
					int alt36=8;
					switch ( input.LA(1) ) {
					case 73:
						{
						alt36=1;
						}
						break;
					case 39:
						{
						alt36=2;
						}
						break;
					case 66:
						{
						alt36=3;
						}
						break;
					case 38:
						{
						alt36=4;
						}
						break;
					case 86:
						{
						alt36=5;
						}
						break;
					case 84:
						{
						alt36=6;
						}
						break;
					case 43:
						{
						alt36=7;
						}
						break;
					case 41:
						{
						alt36=8;
						}
						break;
					default:
						if (state.backtracking>0) {state.failed=true; return retval;}
						NoViableAltException nvae =
							new NoViableAltException("", 36, 0, input);
						throw nvae;
					}
					switch (alt36) {
						case 1 :
							// CMinus.g:281:9: 'lu'
							{
							match(input,73,FOLLOW_73_in_numericexpr3275); if (state.failed) return retval;
							}
							break;
						case 2 :
							// CMinus.g:281:14: 'Lu'
							{
							match(input,39,FOLLOW_39_in_numericexpr3277); if (state.failed) return retval;
							}
							break;
						case 3 :
							// CMinus.g:281:19: 'lU'
							{
							match(input,66,FOLLOW_66_in_numericexpr3279); if (state.failed) return retval;
							}
							break;
						case 4 :
							// CMinus.g:281:24: 'LU'
							{
							match(input,38,FOLLOW_38_in_numericexpr3281); if (state.failed) return retval;
							}
							break;
						case 5 :
							// CMinus.g:281:29: 'ul'
							{
							match(input,86,FOLLOW_86_in_numericexpr3283); if (state.failed) return retval;
							}
							break;
						case 6 :
							// CMinus.g:281:34: 'uL'
							{
							match(input,84,FOLLOW_84_in_numericexpr3285); if (state.failed) return retval;
							}
							break;
						case 7 :
							// CMinus.g:281:39: 'Ul'
							{
							match(input,43,FOLLOW_43_in_numericexpr3287); if (state.failed) return retval;
							}
							break;
						case 8 :
							// CMinus.g:281:44: 'UL'
							{
							match(input,41,FOLLOW_41_in_numericexpr3289); if (state.failed) return retval;
							}
							break;

					}

					// TEMPLATE REWRITE
					if ( state.backtracking==0 ) {
					  // 281:50: -> unsignedLongInt(value=$INT.text)
					  {
					  	retval.st = templateLib.getInstanceOf("unsignedLongInt",new STAttrMap().put("value", (INT75!=null?INT75.getText():null)));
					  }


					}

					}
					break;
				case 7 :
					// CMinus.g:282:4: INT ( 'llu' | 'LLu' | 'llU' | 'LLU' | 'ull' | 'uLL' | 'Ull' | 'ULL' )
					{
					INT76=(Token)match(input,INT,FOLLOW_INT_in_numericexpr3304); if (state.failed) return retval;
					// CMinus.g:282:8: ( 'llu' | 'LLu' | 'llU' | 'LLU' | 'ull' | 'uLL' | 'Ull' | 'ULL' )
					int alt37=8;
					switch ( input.LA(1) ) {
					case 69:
						{
						alt37=1;
						}
						break;
					case 37:
						{
						alt37=2;
						}
						break;
					case 68:
						{
						alt37=3;
						}
						break;
					case 36:
						{
						alt37=4;
						}
						break;
					case 87:
						{
						alt37=5;
						}
						break;
					case 85:
						{
						alt37=6;
						}
						break;
					case 44:
						{
						alt37=7;
						}
						break;
					case 42:
						{
						alt37=8;
						}
						break;
					default:
						if (state.backtracking>0) {state.failed=true; return retval;}
						NoViableAltException nvae =
							new NoViableAltException("", 37, 0, input);
						throw nvae;
					}
					switch (alt37) {
						case 1 :
							// CMinus.g:282:9: 'llu'
							{
							match(input,69,FOLLOW_69_in_numericexpr3307); if (state.failed) return retval;
							}
							break;
						case 2 :
							// CMinus.g:282:15: 'LLu'
							{
							match(input,37,FOLLOW_37_in_numericexpr3309); if (state.failed) return retval;
							}
							break;
						case 3 :
							// CMinus.g:282:21: 'llU'
							{
							match(input,68,FOLLOW_68_in_numericexpr3311); if (state.failed) return retval;
							}
							break;
						case 4 :
							// CMinus.g:282:27: 'LLU'
							{
							match(input,36,FOLLOW_36_in_numericexpr3313); if (state.failed) return retval;
							}
							break;
						case 5 :
							// CMinus.g:282:33: 'ull'
							{
							match(input,87,FOLLOW_87_in_numericexpr3315); if (state.failed) return retval;
							}
							break;
						case 6 :
							// CMinus.g:282:39: 'uLL'
							{
							match(input,85,FOLLOW_85_in_numericexpr3317); if (state.failed) return retval;
							}
							break;
						case 7 :
							// CMinus.g:282:45: 'Ull'
							{
							match(input,44,FOLLOW_44_in_numericexpr3319); if (state.failed) return retval;
							}
							break;
						case 8 :
							// CMinus.g:282:51: 'ULL'
							{
							match(input,42,FOLLOW_42_in_numericexpr3321); if (state.failed) return retval;
							}
							break;

					}

					// TEMPLATE REWRITE
					if ( state.backtracking==0 ) {
					  // 282:58: -> unsignedLongLongInt(value=$INT.text)
					  {
					  	retval.st = templateLib.getInstanceOf("unsignedLongLongInt",new STAttrMap().put("value", (INT76!=null?INT76.getText():null)));
					  }


					}

					}
					break;
				case 8 :
					// CMinus.g:283:4: INT
					{
					INT77=(Token)match(input,INT,FOLLOW_INT_in_numericexpr3336); if (state.failed) return retval;
					// TEMPLATE REWRITE
					if ( state.backtracking==0 ) {
					  // 283:8: -> defaultInt(value=$INT.text)
					  {
					  	retval.st = templateLib.getInstanceOf("defaultInt",new STAttrMap().put("value", (INT77!=null?INT77.getText():null)));
					  }


					}

					}
					break;

			}
			retval.stop = input.LT(-1);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "numericexpr"

	// $ANTLR start synpred3_CMinus
	public final void synpred3_CMinus_fragment() throws RecognitionException {
		// CMinus.g:45:9: ( type declarator ';' )
		// CMinus.g:45:9: type declarator ';'
		{
		pushFollow(FOLLOW_type_in_synpred3_CMinus142);
		type();
		state._fsp--;
		if (state.failed) return;
		pushFollow(FOLLOW_declarator_in_synpred3_CMinus144);
		declarator();
		state._fsp--;
		if (state.failed) return;
		match(input,24,FOLLOW_24_in_synpred3_CMinus146); if (state.failed) return;
		}

	}
	// $ANTLR end synpred3_CMinus

	// $ANTLR start synpred4_CMinus
	public final void synpred4_CMinus_fragment() throws RecognitionException {
		// CMinus.g:49:6: ( type declarator '=' expr ';' )
		// CMinus.g:49:6: type declarator '=' expr ';'
		{
		pushFollow(FOLLOW_type_in_synpred4_CMinus208);
		type();
		state._fsp--;
		if (state.failed) return;
		pushFollow(FOLLOW_declarator_in_synpred4_CMinus210);
		declarator();
		state._fsp--;
		if (state.failed) return;
		match(input,28,FOLLOW_28_in_synpred4_CMinus212); if (state.failed) return;
		pushFollow(FOLLOW_expr_in_synpred4_CMinus214);
		expr();
		state._fsp--;
		if (state.failed) return;
		match(input,24,FOLLOW_24_in_synpred4_CMinus216); if (state.failed) return;
		}

	}
	// $ANTLR end synpred4_CMinus

	// $ANTLR start synpred5_CMinus
	public final void synpred5_CMinus_fragment() throws RecognitionException {
		// CMinus.g:53:6: ( type declarator '[' aexpr ']' ';' )
		// CMinus.g:53:6: type declarator '[' aexpr ']' ';'
		{
		pushFollow(FOLLOW_type_in_synpred5_CMinus283);
		type();
		state._fsp--;
		if (state.failed) return;
		pushFollow(FOLLOW_declarator_in_synpred5_CMinus285);
		declarator();
		state._fsp--;
		if (state.failed) return;
		match(input,45,FOLLOW_45_in_synpred5_CMinus287); if (state.failed) return;
		pushFollow(FOLLOW_aexpr_in_synpred5_CMinus289);
		aexpr();
		state._fsp--;
		if (state.failed) return;
		match(input,48,FOLLOW_48_in_synpred5_CMinus291); if (state.failed) return;
		match(input,24,FOLLOW_24_in_synpred5_CMinus293); if (state.failed) return;
		}

	}
	// $ANTLR end synpred5_CMinus

	// $ANTLR start synpred6_CMinus
	public final void synpred6_CMinus_fragment() throws RecognitionException {
		// CMinus.g:57:6: ( type declarator '[' aexpr ']' '=' arrayinit ';' )
		// CMinus.g:57:6: type declarator '[' aexpr ']' '=' arrayinit ';'
		{
		pushFollow(FOLLOW_type_in_synpred6_CMinus360);
		type();
		state._fsp--;
		if (state.failed) return;
		pushFollow(FOLLOW_declarator_in_synpred6_CMinus362);
		declarator();
		state._fsp--;
		if (state.failed) return;
		match(input,45,FOLLOW_45_in_synpred6_CMinus364); if (state.failed) return;
		pushFollow(FOLLOW_aexpr_in_synpred6_CMinus366);
		aexpr();
		state._fsp--;
		if (state.failed) return;
		match(input,48,FOLLOW_48_in_synpred6_CMinus368); if (state.failed) return;
		match(input,28,FOLLOW_28_in_synpred6_CMinus370); if (state.failed) return;
		pushFollow(FOLLOW_arrayinit_in_synpred6_CMinus372);
		arrayinit();
		state._fsp--;
		if (state.failed) return;
		match(input,24,FOLLOW_24_in_synpred6_CMinus374); if (state.failed) return;
		}

	}
	// $ANTLR end synpred6_CMinus

	// $ANTLR start synpred7_CMinus
	public final void synpred7_CMinus_fragment() throws RecognitionException {
		ParserRuleReturnScope a =null;
		ParserRuleReturnScope b =null;

		// CMinus.g:61:6: ( type declarator '[' a= aexpr ']' '[' b= aexpr ']' ';' )
		// CMinus.g:61:6: type declarator '[' a= aexpr ']' '[' b= aexpr ']' ';'
		{
		pushFollow(FOLLOW_type_in_synpred7_CMinus449);
		type();
		state._fsp--;
		if (state.failed) return;
		pushFollow(FOLLOW_declarator_in_synpred7_CMinus451);
		declarator();
		state._fsp--;
		if (state.failed) return;
		match(input,45,FOLLOW_45_in_synpred7_CMinus453); if (state.failed) return;
		pushFollow(FOLLOW_aexpr_in_synpred7_CMinus457);
		a=aexpr();
		state._fsp--;
		if (state.failed) return;
		match(input,48,FOLLOW_48_in_synpred7_CMinus459); if (state.failed) return;
		match(input,45,FOLLOW_45_in_synpred7_CMinus461); if (state.failed) return;
		pushFollow(FOLLOW_aexpr_in_synpred7_CMinus465);
		b=aexpr();
		state._fsp--;
		if (state.failed) return;
		match(input,48,FOLLOW_48_in_synpred7_CMinus467); if (state.failed) return;
		match(input,24,FOLLOW_24_in_synpred7_CMinus469); if (state.failed) return;
		}

	}
	// $ANTLR end synpred7_CMinus

	// $ANTLR start synpred10_CMinus
	public final void synpred10_CMinus_fragment() throws RecognitionException {
		List<Object> list_p=null;
		RuleReturnScope p = null;
		// CMinus.g:72:9: ( '{' (p+= realnum ( ',' p+= realnum )* )? '}' )
		// CMinus.g:72:9: '{' (p+= realnum ( ',' p+= realnum )* )? '}'
		{
		match(input,89,FOLLOW_89_in_synpred10_CMinus664); if (state.failed) return;
		// CMinus.g:72:13: (p+= realnum ( ',' p+= realnum )* )?
		int alt41=2;
		int LA41_0 = input.LA(1);
		if ( (LA41_0==FP||LA41_0==INT||LA41_0==19) ) {
			alt41=1;
		}
		switch (alt41) {
			case 1 :
				// CMinus.g:72:15: p+= realnum ( ',' p+= realnum )*
				{
				pushFollow(FOLLOW_realnum_in_synpred10_CMinus670);
				p=realnum();
				state._fsp--;
				if (state.failed) return;
				// CMinus.g:72:26: ( ',' p+= realnum )*
				loop40:
				while (true) {
					int alt40=2;
					int LA40_0 = input.LA(1);
					if ( (LA40_0==18) ) {
						alt40=1;
					}

					switch (alt40) {
					case 1 :
						// CMinus.g:72:28: ',' p+= realnum
						{
						match(input,18,FOLLOW_18_in_synpred10_CMinus674); if (state.failed) return;
						pushFollow(FOLLOW_realnum_in_synpred10_CMinus678);
						p=realnum();
						state._fsp--;
						if (state.failed) return;
						}
						break;

					default :
						break loop40;
					}
				}

				}
				break;

		}

		match(input,92,FOLLOW_92_in_synpred10_CMinus686); if (state.failed) return;
		}

	}
	// $ANTLR end synpred10_CMinus

	// $ANTLR start synpred29_CMinus
	public final void synpred29_CMinus_fragment() throws RecognitionException {
		// CMinus.g:134:4: ( constructExpr ';' )
		// CMinus.g:134:4: constructExpr ';'
		{
		pushFollow(FOLLOW_constructExpr_in_synpred29_CMinus1283);
		constructExpr();
		state._fsp--;
		if (state.failed) return;
		match(input,24,FOLLOW_24_in_synpred29_CMinus1285); if (state.failed) return;
		}

	}
	// $ANTLR end synpred29_CMinus

	// $ANTLR start synpred30_CMinus
	public final void synpred30_CMinus_fragment() throws RecognitionException {
		// CMinus.g:135:4: ( assignStat ';' )
		// CMinus.g:135:4: assignStat ';'
		{
		pushFollow(FOLLOW_assignStat_in_synpred30_CMinus1294);
		assignStat();
		state._fsp--;
		if (state.failed) return;
		match(input,24,FOLLOW_24_in_synpred30_CMinus1296); if (state.failed) return;
		}

	}
	// $ANTLR end synpred30_CMinus

	// $ANTLR start synpred33_CMinus
	public final void synpred33_CMinus_fragment() throws RecognitionException {
		// CMinus.g:138:7: ( expr ';' )
		// CMinus.g:138:7: expr ';'
		{
		pushFollow(FOLLOW_expr_in_synpred33_CMinus1331);
		expr();
		state._fsp--;
		if (state.failed) return;
		match(input,24,FOLLOW_24_in_synpred33_CMinus1333); if (state.failed) return;
		}

	}
	// $ANTLR end synpred33_CMinus

	// $ANTLR start synpred34_CMinus
	public final void synpred34_CMinus_fragment() throws RecognitionException {
		// CMinus.g:139:7: ( block )
		// CMinus.g:139:7: block
		{
		pushFollow(FOLLOW_block_in_synpred34_CMinus1350);
		block();
		state._fsp--;
		if (state.failed) return;
		}

	}
	// $ANTLR end synpred34_CMinus

	// $ANTLR start synpred36_CMinus
	public final void synpred36_CMinus_fragment() throws RecognitionException {
		ParserRuleReturnScope e1 =null;
		ParserRuleReturnScope e2 =null;

		// CMinus.g:177:6: (e1= expr '=' e2= expr )
		// CMinus.g:177:6: e1= expr '=' e2= expr
		{
		pushFollow(FOLLOW_expr_in_synpred36_CMinus1657);
		e1=expr();
		state._fsp--;
		if (state.failed) return;
		match(input,28,FOLLOW_28_in_synpred36_CMinus1659); if (state.failed) return;
		pushFollow(FOLLOW_expr_in_synpred36_CMinus1663);
		e2=expr();
		state._fsp--;
		if (state.failed) return;
		}

	}
	// $ANTLR end synpred36_CMinus

	// $ANTLR start synpred37_CMinus
	public final void synpred37_CMinus_fragment() throws RecognitionException {
		// CMinus.g:178:6: ( expr '++' )
		// CMinus.g:178:6: expr '++'
		{
		pushFollow(FOLLOW_expr_in_synpred37_CMinus1684);
		expr();
		state._fsp--;
		if (state.failed) return;
		match(input,16,FOLLOW_16_in_synpred37_CMinus1686); if (state.failed) return;
		}

	}
	// $ANTLR end synpred37_CMinus

	// $ANTLR start synpred38_CMinus
	public final void synpred38_CMinus_fragment() throws RecognitionException {
		// CMinus.g:179:6: ( expr '--' )
		// CMinus.g:179:6: expr '--'
		{
		pushFollow(FOLLOW_expr_in_synpred38_CMinus1702);
		expr();
		state._fsp--;
		if (state.failed) return;
		match(input,20,FOLLOW_20_in_synpred38_CMinus1704); if (state.failed) return;
		}

	}
	// $ANTLR end synpred38_CMinus

	// $ANTLR start synpred39_CMinus
	public final void synpred39_CMinus_fragment() throws RecognitionException {
		ParserRuleReturnScope e1 =null;
		ParserRuleReturnScope e2 =null;

		// CMinus.g:180:6: (e1= expr '+=' e2= expr )
		// CMinus.g:180:6: e1= expr '+=' e2= expr
		{
		pushFollow(FOLLOW_expr_in_synpred39_CMinus1722);
		e1=expr();
		state._fsp--;
		if (state.failed) return;
		match(input,17,FOLLOW_17_in_synpred39_CMinus1724); if (state.failed) return;
		pushFollow(FOLLOW_expr_in_synpred39_CMinus1728);
		e2=expr();
		state._fsp--;
		if (state.failed) return;
		}

	}
	// $ANTLR end synpred39_CMinus

	// $ANTLR start synpred40_CMinus
	public final void synpred40_CMinus_fragment() throws RecognitionException {
		ParserRuleReturnScope e1 =null;
		ParserRuleReturnScope e2 =null;

		// CMinus.g:181:6: (e1= expr '-=' e2= expr )
		// CMinus.g:181:6: e1= expr '-=' e2= expr
		{
		pushFollow(FOLLOW_expr_in_synpred40_CMinus1751);
		e1=expr();
		state._fsp--;
		if (state.failed) return;
		match(input,21,FOLLOW_21_in_synpred40_CMinus1753); if (state.failed) return;
		pushFollow(FOLLOW_expr_in_synpred40_CMinus1757);
		e2=expr();
		state._fsp--;
		if (state.failed) return;
		}

	}
	// $ANTLR end synpred40_CMinus

	// $ANTLR start synpred41_CMinus
	public final void synpred41_CMinus_fragment() throws RecognitionException {
		ParserRuleReturnScope e1 =null;
		ParserRuleReturnScope e2 =null;

		// CMinus.g:182:6: (e1= expr '*=' e2= expr )
		// CMinus.g:182:6: e1= expr '*=' e2= expr
		{
		pushFollow(FOLLOW_expr_in_synpred41_CMinus1780);
		e1=expr();
		state._fsp--;
		if (state.failed) return;
		match(input,14,FOLLOW_14_in_synpred41_CMinus1782); if (state.failed) return;
		pushFollow(FOLLOW_expr_in_synpred41_CMinus1786);
		e2=expr();
		state._fsp--;
		if (state.failed) return;
		}

	}
	// $ANTLR end synpred41_CMinus

	// $ANTLR start synpred42_CMinus
	public final void synpred42_CMinus_fragment() throws RecognitionException {
		// CMinus.g:186:9: ( condExpr )
		// CMinus.g:186:9: condExpr
		{
		pushFollow(FOLLOW_condExpr_in_synpred42_CMinus1843);
		condExpr();
		state._fsp--;
		if (state.failed) return;
		}

	}
	// $ANTLR end synpred42_CMinus

	// $ANTLR start synpred43_CMinus
	public final void synpred43_CMinus_fragment() throws RecognitionException {
		// CMinus.g:187:6: ( aexpr )
		// CMinus.g:187:6: aexpr
		{
		pushFollow(FOLLOW_aexpr_in_synpred43_CMinus1854);
		aexpr();
		state._fsp--;
		if (state.failed) return;
		}

	}
	// $ANTLR end synpred43_CMinus

	// $ANTLR start synpred64_CMinus
	public final void synpred64_CMinus_fragment() throws RecognitionException {
		ParserRuleReturnScope c1 =null;
		ParserRuleReturnScope c2 =null;

		// CMinus.g:223:6: (c1= condexp '&&' c2= condexp )
		// CMinus.g:223:6: c1= condexp '&&' c2= condexp
		{
		pushFollow(FOLLOW_condexp_in_synpred64_CMinus2372);
		c1=condexp();
		state._fsp--;
		if (state.failed) return;
		match(input,9,FOLLOW_9_in_synpred64_CMinus2374); if (state.failed) return;
		pushFollow(FOLLOW_condexp_in_synpred64_CMinus2378);
		c2=condexp();
		state._fsp--;
		if (state.failed) return;
		}

	}
	// $ANTLR end synpred64_CMinus

	// $ANTLR start synpred65_CMinus
	public final void synpred65_CMinus_fragment() throws RecognitionException {
		ParserRuleReturnScope c1 =null;
		ParserRuleReturnScope c2 =null;

		// CMinus.g:224:6: (c1= condexp '||' c2= condexp )
		// CMinus.g:224:6: c1= condexp '||' c2= condexp
		{
		pushFollow(FOLLOW_condexp_in_synpred65_CMinus2400);
		c1=condexp();
		state._fsp--;
		if (state.failed) return;
		match(input,91,FOLLOW_91_in_synpred65_CMinus2402); if (state.failed) return;
		pushFollow(FOLLOW_condexp_in_synpred65_CMinus2406);
		c2=condexp();
		state._fsp--;
		if (state.failed) return;
		}

	}
	// $ANTLR end synpred65_CMinus

	// $ANTLR start synpred74_CMinus
	public final void synpred74_CMinus_fragment() throws RecognitionException {
		// CMinus.g:244:6: ( '(' type ')' atom )
		// CMinus.g:244:6: '(' type ')' atom
		{
		match(input,11,FOLLOW_11_in_synpred74_CMinus2708); if (state.failed) return;
		pushFollow(FOLLOW_type_in_synpred74_CMinus2710);
		type();
		state._fsp--;
		if (state.failed) return;
		match(input,12,FOLLOW_12_in_synpred74_CMinus2712); if (state.failed) return;
		pushFollow(FOLLOW_atom_in_synpred74_CMinus2714);
		atom();
		state._fsp--;
		if (state.failed) return;
		}

	}
	// $ANTLR end synpred74_CMinus

	// Delegated rules

	public final boolean synpred10_CMinus() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred10_CMinus_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred74_CMinus() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred74_CMinus_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred39_CMinus() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred39_CMinus_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred64_CMinus() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred64_CMinus_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred4_CMinus() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred4_CMinus_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred38_CMinus() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred38_CMinus_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred65_CMinus() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred65_CMinus_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred3_CMinus() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred3_CMinus_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred5_CMinus() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred5_CMinus_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred37_CMinus() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred37_CMinus_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred29_CMinus() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred29_CMinus_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred30_CMinus() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred30_CMinus_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred33_CMinus() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred33_CMinus_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred41_CMinus() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred41_CMinus_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred42_CMinus() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred42_CMinus_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred43_CMinus() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred43_CMinus_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred7_CMinus() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred7_CMinus_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred40_CMinus() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred40_CMinus_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred6_CMinus() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred6_CMinus_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred34_CMinus() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred34_CMinus_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred36_CMinus() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred36_CMinus_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}



	public static final BitSet FOLLOW_declaration_in_program61 = new BitSet(new long[]{0x1090000000000022L,0x0000000001000101L});
	public static final BitSet FOLLOW_variable_in_declaration102 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_function_in_declaration118 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_type_in_variable142 = new BitSet(new long[]{0x0000000000000020L});
	public static final BitSet FOLLOW_declarator_in_variable144 = new BitSet(new long[]{0x0000000001000000L});
	public static final BitSet FOLLOW_24_in_variable146 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_type_in_variable208 = new BitSet(new long[]{0x0000000000000020L});
	public static final BitSet FOLLOW_declarator_in_variable210 = new BitSet(new long[]{0x0000000010000000L});
	public static final BitSet FOLLOW_28_in_variable212 = new BitSet(new long[]{0x2A6C000000080870L,0x000000000207F4C0L});
	public static final BitSet FOLLOW_expr_in_variable214 = new BitSet(new long[]{0x0000000001000000L});
	public static final BitSet FOLLOW_24_in_variable216 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_type_in_variable283 = new BitSet(new long[]{0x0000000000000020L});
	public static final BitSet FOLLOW_declarator_in_variable285 = new BitSet(new long[]{0x0000200000000000L});
	public static final BitSet FOLLOW_45_in_variable287 = new BitSet(new long[]{0x226C000000080870L,0x000000000003F4C0L});
	public static final BitSet FOLLOW_aexpr_in_variable289 = new BitSet(new long[]{0x0001000000000000L});
	public static final BitSet FOLLOW_48_in_variable291 = new BitSet(new long[]{0x0000000001000000L});
	public static final BitSet FOLLOW_24_in_variable293 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_type_in_variable360 = new BitSet(new long[]{0x0000000000000020L});
	public static final BitSet FOLLOW_declarator_in_variable362 = new BitSet(new long[]{0x0000200000000000L});
	public static final BitSet FOLLOW_45_in_variable364 = new BitSet(new long[]{0x226C000000080870L,0x000000000003F4C0L});
	public static final BitSet FOLLOW_aexpr_in_variable366 = new BitSet(new long[]{0x0001000000000000L});
	public static final BitSet FOLLOW_48_in_variable368 = new BitSet(new long[]{0x0000000010000000L});
	public static final BitSet FOLLOW_28_in_variable370 = new BitSet(new long[]{0x0000000000000000L,0x0000000002000000L});
	public static final BitSet FOLLOW_arrayinit_in_variable372 = new BitSet(new long[]{0x0000000001000000L});
	public static final BitSet FOLLOW_24_in_variable374 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_type_in_variable449 = new BitSet(new long[]{0x0000000000000020L});
	public static final BitSet FOLLOW_declarator_in_variable451 = new BitSet(new long[]{0x0000200000000000L});
	public static final BitSet FOLLOW_45_in_variable453 = new BitSet(new long[]{0x226C000000080870L,0x000000000003F4C0L});
	public static final BitSet FOLLOW_aexpr_in_variable457 = new BitSet(new long[]{0x0001000000000000L});
	public static final BitSet FOLLOW_48_in_variable459 = new BitSet(new long[]{0x0000200000000000L});
	public static final BitSet FOLLOW_45_in_variable461 = new BitSet(new long[]{0x226C000000080870L,0x000000000003F4C0L});
	public static final BitSet FOLLOW_aexpr_in_variable465 = new BitSet(new long[]{0x0001000000000000L});
	public static final BitSet FOLLOW_48_in_variable467 = new BitSet(new long[]{0x0000000001000000L});
	public static final BitSet FOLLOW_24_in_variable469 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_type_in_variable544 = new BitSet(new long[]{0x0000000000000020L});
	public static final BitSet FOLLOW_declarator_in_variable546 = new BitSet(new long[]{0x0000200000000000L});
	public static final BitSet FOLLOW_45_in_variable548 = new BitSet(new long[]{0x226C000000080870L,0x000000000003F4C0L});
	public static final BitSet FOLLOW_aexpr_in_variable552 = new BitSet(new long[]{0x0001000000000000L});
	public static final BitSet FOLLOW_48_in_variable554 = new BitSet(new long[]{0x0000200000000000L});
	public static final BitSet FOLLOW_45_in_variable556 = new BitSet(new long[]{0x226C000000080870L,0x000000000003F4C0L});
	public static final BitSet FOLLOW_aexpr_in_variable560 = new BitSet(new long[]{0x0001000000000000L});
	public static final BitSet FOLLOW_48_in_variable562 = new BitSet(new long[]{0x0000000010000000L});
	public static final BitSet FOLLOW_28_in_variable564 = new BitSet(new long[]{0x0000000000000000L,0x0000000002000000L});
	public static final BitSet FOLLOW_arrayinit_in_variable566 = new BitSet(new long[]{0x0000000001000000L});
	public static final BitSet FOLLOW_24_in_variable568 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_89_in_arrayinit664 = new BitSet(new long[]{0x0000000000080050L,0x0000000010000000L});
	public static final BitSet FOLLOW_realnum_in_arrayinit670 = new BitSet(new long[]{0x0000000000040000L,0x0000000010000000L});
	public static final BitSet FOLLOW_18_in_arrayinit674 = new BitSet(new long[]{0x0000000000080050L});
	public static final BitSet FOLLOW_realnum_in_arrayinit678 = new BitSet(new long[]{0x0000000000040000L,0x0000000010000000L});
	public static final BitSet FOLLOW_92_in_arrayinit686 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_89_in_arrayinit702 = new BitSet(new long[]{0x0000000000000000L,0x0000000012000000L});
	public static final BitSet FOLLOW_arrayinit_in_arrayinit708 = new BitSet(new long[]{0x0000000000040000L,0x0000000010000000L});
	public static final BitSet FOLLOW_18_in_arrayinit712 = new BitSet(new long[]{0x0000000000000000L,0x0000000002000000L});
	public static final BitSet FOLLOW_arrayinit_in_arrayinit716 = new BitSet(new long[]{0x0000000000040000L,0x0000000010000000L});
	public static final BitSet FOLLOW_92_in_arrayinit724 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ID_in_declarator749 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_type_in_function786 = new BitSet(new long[]{0x0000000000000020L});
	public static final BitSet FOLLOW_ID_in_function788 = new BitSet(new long[]{0x0000000000000800L});
	public static final BitSet FOLLOW_11_in_function800 = new BitSet(new long[]{0x1090000000001020L,0x0000000001000101L});
	public static final BitSet FOLLOW_formalParameter_in_function806 = new BitSet(new long[]{0x0000000000041000L});
	public static final BitSet FOLLOW_18_in_function810 = new BitSet(new long[]{0x1090000000000020L,0x0000000001000101L});
	public static final BitSet FOLLOW_formalParameter_in_function814 = new BitSet(new long[]{0x0000000000041000L});
	public static final BitSet FOLLOW_12_in_function822 = new BitSet(new long[]{0x0000000000000000L,0x0000000002000000L});
	public static final BitSet FOLLOW_block_in_function832 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_type_in_formalParameter948 = new BitSet(new long[]{0x0000000000000020L});
	public static final BitSet FOLLOW_declarator_in_formalParameter950 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_type_in_formalParameter979 = new BitSet(new long[]{0x0000000000000020L});
	public static final BitSet FOLLOW_declarator_in_formalParameter981 = new BitSet(new long[]{0x0000400000000000L});
	public static final BitSet FOLLOW_46_in_formalParameter983 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_type_in_formalParameter1008 = new BitSet(new long[]{0x0000000000000020L});
	public static final BitSet FOLLOW_declarator_in_formalParameter1010 = new BitSet(new long[]{0x0000800000000000L});
	public static final BitSet FOLLOW_47_in_formalParameter1012 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_88_in_type1046 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000100L});
	public static final BitSet FOLLOW_72_in_type1048 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000100L});
	public static final BitSet FOLLOW_72_in_type1050 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000001L});
	public static final BitSet FOLLOW_64_in_type1052 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_72_in_type1065 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000100L});
	public static final BitSet FOLLOW_72_in_type1067 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000001L});
	public static final BitSet FOLLOW_64_in_type1069 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_88_in_type1082 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000100L});
	public static final BitSet FOLLOW_72_in_type1084 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_88_in_type1097 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000001L});
	public static final BitSet FOLLOW_64_in_type1099 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_64_in_type1112 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_72_in_type1126 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_52_in_type1142 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_60_in_type1155 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_55_in_type1168 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ID_in_type1184 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_89_in_block1215 = new BitSet(new long[]{0xFBFC000001080870L,0x000000001307FDC1L});
	public static final BitSet FOLLOW_variable_in_block1226 = new BitSet(new long[]{0xFBFC000001080870L,0x000000001307FDC1L});
	public static final BitSet FOLLOW_stat_in_block1232 = new BitSet(new long[]{0xFBFC000001080870L,0x000000001307FDC1L});
	public static final BitSet FOLLOW_92_in_block1245 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_forStat_in_stat1274 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_constructExpr_in_stat1283 = new BitSet(new long[]{0x0000000001000000L});
	public static final BitSet FOLLOW_24_in_stat1285 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_assignStat_in_stat1294 = new BitSet(new long[]{0x0000000001000000L});
	public static final BitSet FOLLOW_24_in_stat1296 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ifStat_in_stat1310 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_elseStat_in_stat1319 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_expr_in_stat1331 = new BitSet(new long[]{0x0000000001000000L});
	public static final BitSet FOLLOW_24_in_stat1333 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_block_in_stat1350 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_75_in_stat1369 = new BitSet(new long[]{0x2A6C000000080870L,0x000000000207F4C0L});
	public static final BitSet FOLLOW_expr_in_stat1371 = new BitSet(new long[]{0x0000000001000000L});
	public static final BitSet FOLLOW_24_in_stat1373 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_24_in_stat1390 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_63_in_ifStat1424 = new BitSet(new long[]{0x0000000000000800L});
	public static final BitSet FOLLOW_11_in_ifStat1426 = new BitSet(new long[]{0x2A6C000000080870L,0x000000000007F4C0L});
	public static final BitSet FOLLOW_condExpr_in_ifStat1430 = new BitSet(new long[]{0x0000000000001000L});
	public static final BitSet FOLLOW_12_in_ifStat1432 = new BitSet(new long[]{0x0000000000000000L,0x0000000002000000L});
	public static final BitSet FOLLOW_block_in_ifStat1434 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_56_in_elseStat1510 = new BitSet(new long[]{0x0000000000000000L,0x0000000002000000L});
	public static final BitSet FOLLOW_block_in_elseStat1512 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_62_in_forStat1563 = new BitSet(new long[]{0x0000000000000800L});
	public static final BitSet FOLLOW_11_in_forStat1565 = new BitSet(new long[]{0x2A6C000000080870L,0x000000000207F4C0L});
	public static final BitSet FOLLOW_assignStat_in_forStat1569 = new BitSet(new long[]{0x0000000001000000L});
	public static final BitSet FOLLOW_24_in_forStat1571 = new BitSet(new long[]{0x2A6C000000080870L,0x000000000207F4C0L});
	public static final BitSet FOLLOW_expr_in_forStat1575 = new BitSet(new long[]{0x0000000001000000L});
	public static final BitSet FOLLOW_24_in_forStat1577 = new BitSet(new long[]{0x2A6C000000080870L,0x000000000207F4C0L});
	public static final BitSet FOLLOW_assignStat_in_forStat1581 = new BitSet(new long[]{0x0000000000001000L});
	public static final BitSet FOLLOW_12_in_forStat1583 = new BitSet(new long[]{0x0000000000000000L,0x0000000002000000L});
	public static final BitSet FOLLOW_block_in_forStat1585 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_expr_in_assignStat1657 = new BitSet(new long[]{0x0000000010000000L});
	public static final BitSet FOLLOW_28_in_assignStat1659 = new BitSet(new long[]{0x2A6C000000080870L,0x000000000207F4C0L});
	public static final BitSet FOLLOW_expr_in_assignStat1663 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_expr_in_assignStat1684 = new BitSet(new long[]{0x0000000000010000L});
	public static final BitSet FOLLOW_16_in_assignStat1686 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_expr_in_assignStat1702 = new BitSet(new long[]{0x0000000000100000L});
	public static final BitSet FOLLOW_20_in_assignStat1704 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_expr_in_assignStat1722 = new BitSet(new long[]{0x0000000000020000L});
	public static final BitSet FOLLOW_17_in_assignStat1724 = new BitSet(new long[]{0x2A6C000000080870L,0x000000000207F4C0L});
	public static final BitSet FOLLOW_expr_in_assignStat1728 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_expr_in_assignStat1751 = new BitSet(new long[]{0x0000000000200000L});
	public static final BitSet FOLLOW_21_in_assignStat1753 = new BitSet(new long[]{0x2A6C000000080870L,0x000000000207F4C0L});
	public static final BitSet FOLLOW_expr_in_assignStat1757 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_expr_in_assignStat1780 = new BitSet(new long[]{0x0000000000004000L});
	public static final BitSet FOLLOW_14_in_assignStat1782 = new BitSet(new long[]{0x2A6C000000080870L,0x000000000207F4C0L});
	public static final BitSet FOLLOW_expr_in_assignStat1786 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_expr_in_assignStat1809 = new BitSet(new long[]{0x0000000000800000L});
	public static final BitSet FOLLOW_23_in_assignStat1811 = new BitSet(new long[]{0x2A6C000000080870L,0x000000000207F4C0L});
	public static final BitSet FOLLOW_expr_in_assignStat1815 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_condExpr_in_expr1843 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_aexpr_in_expr1854 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_arrayinit_in_expr1865 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ID_in_arrayexpr1886 = new BitSet(new long[]{0x0000200000000000L});
	public static final BitSet FOLLOW_45_in_arrayexpr1889 = new BitSet(new long[]{0x226C000000080870L,0x000000000003F4C0L});
	public static final BitSet FOLLOW_aexpr_in_arrayexpr1893 = new BitSet(new long[]{0x0001000000000000L});
	public static final BitSet FOLLOW_48_in_arrayexpr1895 = new BitSet(new long[]{0x0000200000000002L});
	public static final BitSet FOLLOW_enumeratedFunction_in_funcexpr1922 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ID_in_funcexpr1936 = new BitSet(new long[]{0x0000000000000800L});
	public static final BitSet FOLLOW_11_in_funcexpr1938 = new BitSet(new long[]{0x226C000000081870L,0x000000000003F4C0L});
	public static final BitSet FOLLOW_aexpr_in_funcexpr1944 = new BitSet(new long[]{0x0000000000041000L});
	public static final BitSet FOLLOW_18_in_funcexpr1948 = new BitSet(new long[]{0x226C000000080870L,0x000000000003F4C0L});
	public static final BitSet FOLLOW_aexpr_in_funcexpr1952 = new BitSet(new long[]{0x0000000000041000L});
	public static final BitSet FOLLOW_12_in_funcexpr1960 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_61_in_enumeratedFunction1985 = new BitSet(new long[]{0x0000000000000800L});
	public static final BitSet FOLLOW_11_in_enumeratedFunction1987 = new BitSet(new long[]{0x226C000000080870L,0x000000000003F4C0L});
	public static final BitSet FOLLOW_aexpr_in_enumeratedFunction1989 = new BitSet(new long[]{0x0000000000001000L});
	public static final BitSet FOLLOW_12_in_enumeratedFunction1991 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_76_in_enumeratedFunction2005 = new BitSet(new long[]{0x0000000000000800L});
	public static final BitSet FOLLOW_11_in_enumeratedFunction2007 = new BitSet(new long[]{0x226C000000080870L,0x000000000003F4C0L});
	public static final BitSet FOLLOW_aexpr_in_enumeratedFunction2009 = new BitSet(new long[]{0x0000000000001000L});
	public static final BitSet FOLLOW_12_in_enumeratedFunction2011 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_51_in_enumeratedFunction2025 = new BitSet(new long[]{0x0000000000000800L});
	public static final BitSet FOLLOW_11_in_enumeratedFunction2027 = new BitSet(new long[]{0x226C000000080870L,0x000000000003F4C0L});
	public static final BitSet FOLLOW_aexpr_in_enumeratedFunction2029 = new BitSet(new long[]{0x0000000000001000L});
	public static final BitSet FOLLOW_12_in_enumeratedFunction2031 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_77_in_enumeratedFunction2045 = new BitSet(new long[]{0x0000000000000800L});
	public static final BitSet FOLLOW_11_in_enumeratedFunction2047 = new BitSet(new long[]{0x226C000000080870L,0x000000000003F4C0L});
	public static final BitSet FOLLOW_aexpr_in_enumeratedFunction2049 = new BitSet(new long[]{0x0000000000001000L});
	public static final BitSet FOLLOW_12_in_enumeratedFunction2051 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_53_in_enumeratedFunction2065 = new BitSet(new long[]{0x0000000000000800L});
	public static final BitSet FOLLOW_11_in_enumeratedFunction2067 = new BitSet(new long[]{0x226C000000080870L,0x000000000003F4C0L});
	public static final BitSet FOLLOW_aexpr_in_enumeratedFunction2069 = new BitSet(new long[]{0x0000000000001000L});
	public static final BitSet FOLLOW_12_in_enumeratedFunction2071 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_54_in_enumeratedFunction2085 = new BitSet(new long[]{0x0000000000000800L});
	public static final BitSet FOLLOW_11_in_enumeratedFunction2087 = new BitSet(new long[]{0x226C000000080870L,0x000000000003F4C0L});
	public static final BitSet FOLLOW_aexpr_in_enumeratedFunction2089 = new BitSet(new long[]{0x0000000000001000L});
	public static final BitSet FOLLOW_12_in_enumeratedFunction2091 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_57_in_enumeratedFunction2105 = new BitSet(new long[]{0x0000000000000800L});
	public static final BitSet FOLLOW_11_in_enumeratedFunction2107 = new BitSet(new long[]{0x226C000000080870L,0x000000000003F4C0L});
	public static final BitSet FOLLOW_aexpr_in_enumeratedFunction2109 = new BitSet(new long[]{0x0000000000001000L});
	public static final BitSet FOLLOW_12_in_enumeratedFunction2111 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_80_in_enumeratedFunction2125 = new BitSet(new long[]{0x0000000000000800L});
	public static final BitSet FOLLOW_11_in_enumeratedFunction2127 = new BitSet(new long[]{0x226C000000080870L,0x000000000003F4C0L});
	public static final BitSet FOLLOW_aexpr_in_enumeratedFunction2129 = new BitSet(new long[]{0x0000000000001000L});
	public static final BitSet FOLLOW_12_in_enumeratedFunction2131 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_81_in_enumeratedFunction2145 = new BitSet(new long[]{0x0000000000000800L});
	public static final BitSet FOLLOW_11_in_enumeratedFunction2147 = new BitSet(new long[]{0x226C000000080870L,0x000000000003F4C0L});
	public static final BitSet FOLLOW_aexpr_in_enumeratedFunction2149 = new BitSet(new long[]{0x0000000000001000L});
	public static final BitSet FOLLOW_12_in_enumeratedFunction2151 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_78_in_enumeratedFunction2165 = new BitSet(new long[]{0x0000000000000800L});
	public static final BitSet FOLLOW_11_in_enumeratedFunction2167 = new BitSet(new long[]{0x226C000000080870L,0x000000000003F4C0L});
	public static final BitSet FOLLOW_aexpr_in_enumeratedFunction2169 = new BitSet(new long[]{0x0000000000001000L});
	public static final BitSet FOLLOW_12_in_enumeratedFunction2171 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_70_in_enumeratedFunction2185 = new BitSet(new long[]{0x0000000000000800L});
	public static final BitSet FOLLOW_11_in_enumeratedFunction2187 = new BitSet(new long[]{0x226C000000080870L,0x000000000003F4C0L});
	public static final BitSet FOLLOW_aexpr_in_enumeratedFunction2189 = new BitSet(new long[]{0x0000000000001000L});
	public static final BitSet FOLLOW_12_in_enumeratedFunction2191 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_71_in_enumeratedFunction2205 = new BitSet(new long[]{0x0000000000000800L});
	public static final BitSet FOLLOW_11_in_enumeratedFunction2207 = new BitSet(new long[]{0x226C000000080870L,0x000000000003F4C0L});
	public static final BitSet FOLLOW_aexpr_in_enumeratedFunction2209 = new BitSet(new long[]{0x0000000000001000L});
	public static final BitSet FOLLOW_12_in_enumeratedFunction2211 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_79_in_enumeratedFunction2225 = new BitSet(new long[]{0x0000000000000800L});
	public static final BitSet FOLLOW_11_in_enumeratedFunction2227 = new BitSet(new long[]{0x226C000000080870L,0x000000000003F4C0L});
	public static final BitSet FOLLOW_aexpr_in_enumeratedFunction2229 = new BitSet(new long[]{0x0000000000001000L});
	public static final BitSet FOLLOW_12_in_enumeratedFunction2231 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_74_in_enumeratedFunction2245 = new BitSet(new long[]{0x0000000000000800L});
	public static final BitSet FOLLOW_11_in_enumeratedFunction2247 = new BitSet(new long[]{0x226C000000080870L,0x000000000003F4C0L});
	public static final BitSet FOLLOW_aexpr_in_enumeratedFunction2251 = new BitSet(new long[]{0x0000000000040000L});
	public static final BitSet FOLLOW_18_in_enumeratedFunction2253 = new BitSet(new long[]{0x226C000000080870L,0x000000000003F4C0L});
	public static final BitSet FOLLOW_aexpr_in_enumeratedFunction2257 = new BitSet(new long[]{0x0000000000001000L});
	public static final BitSet FOLLOW_12_in_enumeratedFunction2259 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_50_in_enumeratedFunction2277 = new BitSet(new long[]{0x0000000000000800L});
	public static final BitSet FOLLOW_11_in_enumeratedFunction2279 = new BitSet(new long[]{0x226C000000080870L,0x000000000003F4C0L});
	public static final BitSet FOLLOW_aexpr_in_enumeratedFunction2281 = new BitSet(new long[]{0x0000000000001000L});
	public static final BitSet FOLLOW_12_in_enumeratedFunction2283 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ID_in_constructExpr2311 = new BitSet(new long[]{0x0000000000000020L});
	public static final BitSet FOLLOW_ID_in_constructExpr2315 = new BitSet(new long[]{0x0000000000000800L});
	public static final BitSet FOLLOW_11_in_constructExpr2317 = new BitSet(new long[]{0x226C000000081870L,0x000000000003F4C0L});
	public static final BitSet FOLLOW_aexpr_in_constructExpr2323 = new BitSet(new long[]{0x0000000000041000L});
	public static final BitSet FOLLOW_18_in_constructExpr2327 = new BitSet(new long[]{0x226C000000080870L,0x000000000003F4C0L});
	public static final BitSet FOLLOW_aexpr_in_constructExpr2331 = new BitSet(new long[]{0x0000000000041000L});
	public static final BitSet FOLLOW_12_in_constructExpr2339 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_condexp_in_condExpr2372 = new BitSet(new long[]{0x0000000000000200L});
	public static final BitSet FOLLOW_9_in_condExpr2374 = new BitSet(new long[]{0x2A6C000000080870L,0x000000000007F4C0L});
	public static final BitSet FOLLOW_condexp_in_condExpr2378 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_condexp_in_condExpr2400 = new BitSet(new long[]{0x0000000000000000L,0x0000000008000000L});
	public static final BitSet FOLLOW_91_in_condExpr2402 = new BitSet(new long[]{0x2A6C000000080870L,0x000000000007F4C0L});
	public static final BitSet FOLLOW_condexp_in_condExpr2406 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_condexp_in_condExpr2428 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_82_in_condexp2443 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_59_in_condexp2454 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_aexpr_in_condexp2472 = new BitSet(new long[]{0x00000000EA000102L});
	public static final BitSet FOLLOW_29_in_condexp2489 = new BitSet(new long[]{0x226C000000080870L,0x000000000003F4C0L});
	public static final BitSet FOLLOW_aexpr_in_condexp2493 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_25_in_condexp2523 = new BitSet(new long[]{0x226C000000080870L,0x000000000003F4C0L});
	public static final BitSet FOLLOW_aexpr_in_condexp2527 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_27_in_condexp2550 = new BitSet(new long[]{0x226C000000080870L,0x000000000003F4C0L});
	public static final BitSet FOLLOW_aexpr_in_condexp2554 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_31_in_condexp2577 = new BitSet(new long[]{0x226C000000080870L,0x000000000003F4C0L});
	public static final BitSet FOLLOW_aexpr_in_condexp2581 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_30_in_condexp2604 = new BitSet(new long[]{0x226C000000080870L,0x000000000003F4C0L});
	public static final BitSet FOLLOW_aexpr_in_condexp2608 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_8_in_condexp2631 = new BitSet(new long[]{0x226C000000080870L,0x000000000003F4C0L});
	public static final BitSet FOLLOW_aexpr_in_condexp2635 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_11_in_basicexpr2708 = new BitSet(new long[]{0x1090000000000020L,0x0000000001000101L});
	public static final BitSet FOLLOW_type_in_basicexpr2710 = new BitSet(new long[]{0x0000000000001000L});
	public static final BitSet FOLLOW_12_in_basicexpr2712 = new BitSet(new long[]{0x226C000000080870L,0x000000000003F4C0L});
	public static final BitSet FOLLOW_atom_in_basicexpr2714 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_atom_in_basicexpr2734 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_basicexpr_in_aexpr2757 = new BitSet(new long[]{0x000200010448A402L,0x0000000004000000L});
	public static final BitSet FOLLOW_15_in_aexpr2775 = new BitSet(new long[]{0x226C000000080870L,0x000000000003F4C0L});
	public static final BitSet FOLLOW_basicexpr_in_aexpr2779 = new BitSet(new long[]{0x000200010448A402L,0x0000000004000000L});
	public static final BitSet FOLLOW_19_in_aexpr2803 = new BitSet(new long[]{0x226C000000080870L,0x000000000003F4C0L});
	public static final BitSet FOLLOW_basicexpr_in_aexpr2807 = new BitSet(new long[]{0x000200010448A402L,0x0000000004000000L});
	public static final BitSet FOLLOW_13_in_aexpr2831 = new BitSet(new long[]{0x226C000000080870L,0x000000000003F4C0L});
	public static final BitSet FOLLOW_basicexpr_in_aexpr2835 = new BitSet(new long[]{0x000200010448A402L,0x0000000004000000L});
	public static final BitSet FOLLOW_22_in_aexpr2859 = new BitSet(new long[]{0x226C000000080870L,0x000000000003F4C0L});
	public static final BitSet FOLLOW_basicexpr_in_aexpr2863 = new BitSet(new long[]{0x000200010448A402L,0x0000000004000000L});
	public static final BitSet FOLLOW_10_in_aexpr2887 = new BitSet(new long[]{0x226C000000080870L,0x000000000003F4C0L});
	public static final BitSet FOLLOW_basicexpr_in_aexpr2891 = new BitSet(new long[]{0x000200010448A402L,0x0000000004000000L});
	public static final BitSet FOLLOW_90_in_aexpr2915 = new BitSet(new long[]{0x226C000000080870L,0x000000000003F4C0L});
	public static final BitSet FOLLOW_basicexpr_in_aexpr2919 = new BitSet(new long[]{0x000200010448A402L,0x0000000004000000L});
	public static final BitSet FOLLOW_49_in_aexpr2943 = new BitSet(new long[]{0x226C000000080870L,0x000000000003F4C0L});
	public static final BitSet FOLLOW_basicexpr_in_aexpr2947 = new BitSet(new long[]{0x000200010448A402L,0x0000000004000000L});
	public static final BitSet FOLLOW_26_in_aexpr2971 = new BitSet(new long[]{0x226C000000080870L,0x000000000003F4C0L});
	public static final BitSet FOLLOW_basicexpr_in_aexpr2975 = new BitSet(new long[]{0x000200010448A402L,0x0000000004000000L});
	public static final BitSet FOLLOW_32_in_aexpr2999 = new BitSet(new long[]{0x226C000000080870L,0x000000000003F4C0L});
	public static final BitSet FOLLOW_basicexpr_in_aexpr3003 = new BitSet(new long[]{0x000200010448A402L,0x0000000004000000L});
	public static final BitSet FOLLOW_arrayexpr_in_atom3038 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_funcexpr_in_atom3047 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ID_in_atom3059 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_numericexpr_in_atom3076 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_11_in_atom3093 = new BitSet(new long[]{0x2A6C000000080870L,0x000000000207F4C0L});
	public static final BitSet FOLLOW_expr_in_atom3095 = new BitSet(new long[]{0x0000000000001000L});
	public static final BitSet FOLLOW_12_in_atom3097 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_19_in_atom3111 = new BitSet(new long[]{0x226C000000080870L,0x000000000003F4C0L});
	public static final BitSet FOLLOW_atom_in_atom3115 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_numericexpr_in_realnum3143 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_19_in_realnum3152 = new BitSet(new long[]{0x0000000000000050L});
	public static final BitSet FOLLOW_numericexpr_in_realnum3154 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_FP_in_numericexpr3178 = new BitSet(new long[]{0x0400000200000000L});
	public static final BitSet FOLLOW_58_in_numericexpr3181 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_33_in_numericexpr3183 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_FP_in_numericexpr3198 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_INT_in_numericexpr3212 = new BitSet(new long[]{0x0000010000000000L,0x0000000000080000L});
	public static final BitSet FOLLOW_83_in_numericexpr3215 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_40_in_numericexpr3217 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_INT_in_numericexpr3232 = new BitSet(new long[]{0x0000000400000000L,0x0000000000000002L});
	public static final BitSet FOLLOW_65_in_numericexpr3235 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_34_in_numericexpr3237 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_INT_in_numericexpr3252 = new BitSet(new long[]{0x0000000800000000L,0x0000000000000008L});
	public static final BitSet FOLLOW_67_in_numericexpr3255 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_35_in_numericexpr3257 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_INT_in_numericexpr3272 = new BitSet(new long[]{0x00000AC000000000L,0x0000000000500204L});
	public static final BitSet FOLLOW_73_in_numericexpr3275 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_39_in_numericexpr3277 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_66_in_numericexpr3279 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_38_in_numericexpr3281 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_86_in_numericexpr3283 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_84_in_numericexpr3285 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_43_in_numericexpr3287 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_41_in_numericexpr3289 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_INT_in_numericexpr3304 = new BitSet(new long[]{0x0000143000000000L,0x0000000000A00030L});
	public static final BitSet FOLLOW_69_in_numericexpr3307 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_37_in_numericexpr3309 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_68_in_numericexpr3311 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_36_in_numericexpr3313 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_87_in_numericexpr3315 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_85_in_numericexpr3317 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_44_in_numericexpr3319 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_42_in_numericexpr3321 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_INT_in_numericexpr3336 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_type_in_synpred3_CMinus142 = new BitSet(new long[]{0x0000000000000020L});
	public static final BitSet FOLLOW_declarator_in_synpred3_CMinus144 = new BitSet(new long[]{0x0000000001000000L});
	public static final BitSet FOLLOW_24_in_synpred3_CMinus146 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_type_in_synpred4_CMinus208 = new BitSet(new long[]{0x0000000000000020L});
	public static final BitSet FOLLOW_declarator_in_synpred4_CMinus210 = new BitSet(new long[]{0x0000000010000000L});
	public static final BitSet FOLLOW_28_in_synpred4_CMinus212 = new BitSet(new long[]{0x2A6C000000080870L,0x000000000207F4C0L});
	public static final BitSet FOLLOW_expr_in_synpred4_CMinus214 = new BitSet(new long[]{0x0000000001000000L});
	public static final BitSet FOLLOW_24_in_synpred4_CMinus216 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_type_in_synpred5_CMinus283 = new BitSet(new long[]{0x0000000000000020L});
	public static final BitSet FOLLOW_declarator_in_synpred5_CMinus285 = new BitSet(new long[]{0x0000200000000000L});
	public static final BitSet FOLLOW_45_in_synpred5_CMinus287 = new BitSet(new long[]{0x226C000000080870L,0x000000000003F4C0L});
	public static final BitSet FOLLOW_aexpr_in_synpred5_CMinus289 = new BitSet(new long[]{0x0001000000000000L});
	public static final BitSet FOLLOW_48_in_synpred5_CMinus291 = new BitSet(new long[]{0x0000000001000000L});
	public static final BitSet FOLLOW_24_in_synpred5_CMinus293 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_type_in_synpred6_CMinus360 = new BitSet(new long[]{0x0000000000000020L});
	public static final BitSet FOLLOW_declarator_in_synpred6_CMinus362 = new BitSet(new long[]{0x0000200000000000L});
	public static final BitSet FOLLOW_45_in_synpred6_CMinus364 = new BitSet(new long[]{0x226C000000080870L,0x000000000003F4C0L});
	public static final BitSet FOLLOW_aexpr_in_synpred6_CMinus366 = new BitSet(new long[]{0x0001000000000000L});
	public static final BitSet FOLLOW_48_in_synpred6_CMinus368 = new BitSet(new long[]{0x0000000010000000L});
	public static final BitSet FOLLOW_28_in_synpred6_CMinus370 = new BitSet(new long[]{0x0000000000000000L,0x0000000002000000L});
	public static final BitSet FOLLOW_arrayinit_in_synpred6_CMinus372 = new BitSet(new long[]{0x0000000001000000L});
	public static final BitSet FOLLOW_24_in_synpred6_CMinus374 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_type_in_synpred7_CMinus449 = new BitSet(new long[]{0x0000000000000020L});
	public static final BitSet FOLLOW_declarator_in_synpred7_CMinus451 = new BitSet(new long[]{0x0000200000000000L});
	public static final BitSet FOLLOW_45_in_synpred7_CMinus453 = new BitSet(new long[]{0x226C000000080870L,0x000000000003F4C0L});
	public static final BitSet FOLLOW_aexpr_in_synpred7_CMinus457 = new BitSet(new long[]{0x0001000000000000L});
	public static final BitSet FOLLOW_48_in_synpred7_CMinus459 = new BitSet(new long[]{0x0000200000000000L});
	public static final BitSet FOLLOW_45_in_synpred7_CMinus461 = new BitSet(new long[]{0x226C000000080870L,0x000000000003F4C0L});
	public static final BitSet FOLLOW_aexpr_in_synpred7_CMinus465 = new BitSet(new long[]{0x0001000000000000L});
	public static final BitSet FOLLOW_48_in_synpred7_CMinus467 = new BitSet(new long[]{0x0000000001000000L});
	public static final BitSet FOLLOW_24_in_synpred7_CMinus469 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_89_in_synpred10_CMinus664 = new BitSet(new long[]{0x0000000000080050L,0x0000000010000000L});
	public static final BitSet FOLLOW_realnum_in_synpred10_CMinus670 = new BitSet(new long[]{0x0000000000040000L,0x0000000010000000L});
	public static final BitSet FOLLOW_18_in_synpred10_CMinus674 = new BitSet(new long[]{0x0000000000080050L});
	public static final BitSet FOLLOW_realnum_in_synpred10_CMinus678 = new BitSet(new long[]{0x0000000000040000L,0x0000000010000000L});
	public static final BitSet FOLLOW_92_in_synpred10_CMinus686 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_constructExpr_in_synpred29_CMinus1283 = new BitSet(new long[]{0x0000000001000000L});
	public static final BitSet FOLLOW_24_in_synpred29_CMinus1285 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_assignStat_in_synpred30_CMinus1294 = new BitSet(new long[]{0x0000000001000000L});
	public static final BitSet FOLLOW_24_in_synpred30_CMinus1296 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_expr_in_synpred33_CMinus1331 = new BitSet(new long[]{0x0000000001000000L});
	public static final BitSet FOLLOW_24_in_synpred33_CMinus1333 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_block_in_synpred34_CMinus1350 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_expr_in_synpred36_CMinus1657 = new BitSet(new long[]{0x0000000010000000L});
	public static final BitSet FOLLOW_28_in_synpred36_CMinus1659 = new BitSet(new long[]{0x2A6C000000080870L,0x000000000207F4C0L});
	public static final BitSet FOLLOW_expr_in_synpred36_CMinus1663 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_expr_in_synpred37_CMinus1684 = new BitSet(new long[]{0x0000000000010000L});
	public static final BitSet FOLLOW_16_in_synpred37_CMinus1686 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_expr_in_synpred38_CMinus1702 = new BitSet(new long[]{0x0000000000100000L});
	public static final BitSet FOLLOW_20_in_synpred38_CMinus1704 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_expr_in_synpred39_CMinus1722 = new BitSet(new long[]{0x0000000000020000L});
	public static final BitSet FOLLOW_17_in_synpred39_CMinus1724 = new BitSet(new long[]{0x2A6C000000080870L,0x000000000207F4C0L});
	public static final BitSet FOLLOW_expr_in_synpred39_CMinus1728 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_expr_in_synpred40_CMinus1751 = new BitSet(new long[]{0x0000000000200000L});
	public static final BitSet FOLLOW_21_in_synpred40_CMinus1753 = new BitSet(new long[]{0x2A6C000000080870L,0x000000000207F4C0L});
	public static final BitSet FOLLOW_expr_in_synpred40_CMinus1757 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_expr_in_synpred41_CMinus1780 = new BitSet(new long[]{0x0000000000004000L});
	public static final BitSet FOLLOW_14_in_synpred41_CMinus1782 = new BitSet(new long[]{0x2A6C000000080870L,0x000000000207F4C0L});
	public static final BitSet FOLLOW_expr_in_synpred41_CMinus1786 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_condExpr_in_synpred42_CMinus1843 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_aexpr_in_synpred43_CMinus1854 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_condexp_in_synpred64_CMinus2372 = new BitSet(new long[]{0x0000000000000200L});
	public static final BitSet FOLLOW_9_in_synpred64_CMinus2374 = new BitSet(new long[]{0x2A6C000000080870L,0x000000000007F4C0L});
	public static final BitSet FOLLOW_condexp_in_synpred64_CMinus2378 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_condexp_in_synpred65_CMinus2400 = new BitSet(new long[]{0x0000000000000000L,0x0000000008000000L});
	public static final BitSet FOLLOW_91_in_synpred65_CMinus2402 = new BitSet(new long[]{0x2A6C000000080870L,0x000000000007F4C0L});
	public static final BitSet FOLLOW_condexp_in_synpred65_CMinus2406 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_11_in_synpred74_CMinus2708 = new BitSet(new long[]{0x1090000000000020L,0x0000000001000101L});
	public static final BitSet FOLLOW_type_in_synpred74_CMinus2710 = new BitSet(new long[]{0x0000000000001000L});
	public static final BitSet FOLLOW_12_in_synpred74_CMinus2712 = new BitSet(new long[]{0x226C000000080870L,0x000000000003F4C0L});
	public static final BitSet FOLLOW_atom_in_synpred74_CMinus2714 = new BitSet(new long[]{0x0000000000000002L});
}
