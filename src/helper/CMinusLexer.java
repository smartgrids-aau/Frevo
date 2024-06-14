package helper;
// $ANTLR 3.5.2 CMinus.g 2019-04-16 15:49:05

import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings("all")
public class CMinusLexer extends Lexer {
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
	// delegators
	public Lexer[] getDelegates() {
		return new Lexer[] {};
	}

	public CMinusLexer() {} 
	public CMinusLexer(CharStream input) {
		this(input, new RecognizerSharedState());
	}
	public CMinusLexer(CharStream input, RecognizerSharedState state) {
		super(input,state);
	}
	@Override public String getGrammarFileName() { return "CMinus.g"; }

	// $ANTLR start "T__8"
	public final void mT__8() throws RecognitionException {
		try {
			int _type = T__8;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// CMinus.g:2:6: ( '!=' )
			// CMinus.g:2:8: '!='
			{
			match("!="); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__8"

	// $ANTLR start "T__9"
	public final void mT__9() throws RecognitionException {
		try {
			int _type = T__9;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// CMinus.g:3:6: ( '&&' )
			// CMinus.g:3:8: '&&'
			{
			match("&&"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__9"

	// $ANTLR start "T__10"
	public final void mT__10() throws RecognitionException {
		try {
			int _type = T__10;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// CMinus.g:4:7: ( '&' )
			// CMinus.g:4:9: '&'
			{
			match('&'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__10"

	// $ANTLR start "T__11"
	public final void mT__11() throws RecognitionException {
		try {
			int _type = T__11;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// CMinus.g:5:7: ( '(' )
			// CMinus.g:5:9: '('
			{
			match('('); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__11"

	// $ANTLR start "T__12"
	public final void mT__12() throws RecognitionException {
		try {
			int _type = T__12;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// CMinus.g:6:7: ( ')' )
			// CMinus.g:6:9: ')'
			{
			match(')'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__12"

	// $ANTLR start "T__13"
	public final void mT__13() throws RecognitionException {
		try {
			int _type = T__13;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// CMinus.g:7:7: ( '*' )
			// CMinus.g:7:9: '*'
			{
			match('*'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__13"

	// $ANTLR start "T__14"
	public final void mT__14() throws RecognitionException {
		try {
			int _type = T__14;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// CMinus.g:8:7: ( '*=' )
			// CMinus.g:8:9: '*='
			{
			match("*="); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__14"

	// $ANTLR start "T__15"
	public final void mT__15() throws RecognitionException {
		try {
			int _type = T__15;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// CMinus.g:9:7: ( '+' )
			// CMinus.g:9:9: '+'
			{
			match('+'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__15"

	// $ANTLR start "T__16"
	public final void mT__16() throws RecognitionException {
		try {
			int _type = T__16;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// CMinus.g:10:7: ( '++' )
			// CMinus.g:10:9: '++'
			{
			match("++"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__16"

	// $ANTLR start "T__17"
	public final void mT__17() throws RecognitionException {
		try {
			int _type = T__17;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// CMinus.g:11:7: ( '+=' )
			// CMinus.g:11:9: '+='
			{
			match("+="); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__17"

	// $ANTLR start "T__18"
	public final void mT__18() throws RecognitionException {
		try {
			int _type = T__18;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// CMinus.g:12:7: ( ',' )
			// CMinus.g:12:9: ','
			{
			match(','); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__18"

	// $ANTLR start "T__19"
	public final void mT__19() throws RecognitionException {
		try {
			int _type = T__19;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// CMinus.g:13:7: ( '-' )
			// CMinus.g:13:9: '-'
			{
			match('-'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__19"

	// $ANTLR start "T__20"
	public final void mT__20() throws RecognitionException {
		try {
			int _type = T__20;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// CMinus.g:14:7: ( '--' )
			// CMinus.g:14:9: '--'
			{
			match("--"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__20"

	// $ANTLR start "T__21"
	public final void mT__21() throws RecognitionException {
		try {
			int _type = T__21;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// CMinus.g:15:7: ( '-=' )
			// CMinus.g:15:9: '-='
			{
			match("-="); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__21"

	// $ANTLR start "T__22"
	public final void mT__22() throws RecognitionException {
		try {
			int _type = T__22;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// CMinus.g:16:7: ( '/' )
			// CMinus.g:16:9: '/'
			{
			match('/'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__22"

	// $ANTLR start "T__23"
	public final void mT__23() throws RecognitionException {
		try {
			int _type = T__23;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// CMinus.g:17:7: ( '/=' )
			// CMinus.g:17:9: '/='
			{
			match("/="); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__23"

	// $ANTLR start "T__24"
	public final void mT__24() throws RecognitionException {
		try {
			int _type = T__24;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// CMinus.g:18:7: ( ';' )
			// CMinus.g:18:9: ';'
			{
			match(';'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__24"

	// $ANTLR start "T__25"
	public final void mT__25() throws RecognitionException {
		try {
			int _type = T__25;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// CMinus.g:19:7: ( '<' )
			// CMinus.g:19:9: '<'
			{
			match('<'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__25"

	// $ANTLR start "T__26"
	public final void mT__26() throws RecognitionException {
		try {
			int _type = T__26;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// CMinus.g:20:7: ( '<<' )
			// CMinus.g:20:9: '<<'
			{
			match("<<"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__26"

	// $ANTLR start "T__27"
	public final void mT__27() throws RecognitionException {
		try {
			int _type = T__27;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// CMinus.g:21:7: ( '<=' )
			// CMinus.g:21:9: '<='
			{
			match("<="); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__27"

	// $ANTLR start "T__28"
	public final void mT__28() throws RecognitionException {
		try {
			int _type = T__28;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// CMinus.g:22:7: ( '=' )
			// CMinus.g:22:9: '='
			{
			match('='); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__28"

	// $ANTLR start "T__29"
	public final void mT__29() throws RecognitionException {
		try {
			int _type = T__29;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// CMinus.g:23:7: ( '==' )
			// CMinus.g:23:9: '=='
			{
			match("=="); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__29"

	// $ANTLR start "T__30"
	public final void mT__30() throws RecognitionException {
		try {
			int _type = T__30;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// CMinus.g:24:7: ( '>' )
			// CMinus.g:24:9: '>'
			{
			match('>'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__30"

	// $ANTLR start "T__31"
	public final void mT__31() throws RecognitionException {
		try {
			int _type = T__31;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// CMinus.g:25:7: ( '>=' )
			// CMinus.g:25:9: '>='
			{
			match(">="); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__31"

	// $ANTLR start "T__32"
	public final void mT__32() throws RecognitionException {
		try {
			int _type = T__32;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// CMinus.g:26:7: ( '>>' )
			// CMinus.g:26:9: '>>'
			{
			match(">>"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__32"

	// $ANTLR start "T__33"
	public final void mT__33() throws RecognitionException {
		try {
			int _type = T__33;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// CMinus.g:27:7: ( 'F' )
			// CMinus.g:27:9: 'F'
			{
			match('F'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__33"

	// $ANTLR start "T__34"
	public final void mT__34() throws RecognitionException {
		try {
			int _type = T__34;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// CMinus.g:28:7: ( 'L' )
			// CMinus.g:28:9: 'L'
			{
			match('L'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__34"

	// $ANTLR start "T__35"
	public final void mT__35() throws RecognitionException {
		try {
			int _type = T__35;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// CMinus.g:29:7: ( 'LL' )
			// CMinus.g:29:9: 'LL'
			{
			match("LL"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__35"

	// $ANTLR start "T__36"
	public final void mT__36() throws RecognitionException {
		try {
			int _type = T__36;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// CMinus.g:30:7: ( 'LLU' )
			// CMinus.g:30:9: 'LLU'
			{
			match("LLU"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__36"

	// $ANTLR start "T__37"
	public final void mT__37() throws RecognitionException {
		try {
			int _type = T__37;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// CMinus.g:31:7: ( 'LLu' )
			// CMinus.g:31:9: 'LLu'
			{
			match("LLu"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__37"

	// $ANTLR start "T__38"
	public final void mT__38() throws RecognitionException {
		try {
			int _type = T__38;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// CMinus.g:32:7: ( 'LU' )
			// CMinus.g:32:9: 'LU'
			{
			match("LU"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__38"

	// $ANTLR start "T__39"
	public final void mT__39() throws RecognitionException {
		try {
			int _type = T__39;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// CMinus.g:33:7: ( 'Lu' )
			// CMinus.g:33:9: 'Lu'
			{
			match("Lu"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__39"

	// $ANTLR start "T__40"
	public final void mT__40() throws RecognitionException {
		try {
			int _type = T__40;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// CMinus.g:34:7: ( 'U' )
			// CMinus.g:34:9: 'U'
			{
			match('U'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__40"

	// $ANTLR start "T__41"
	public final void mT__41() throws RecognitionException {
		try {
			int _type = T__41;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// CMinus.g:35:7: ( 'UL' )
			// CMinus.g:35:9: 'UL'
			{
			match("UL"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__41"

	// $ANTLR start "T__42"
	public final void mT__42() throws RecognitionException {
		try {
			int _type = T__42;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// CMinus.g:36:7: ( 'ULL' )
			// CMinus.g:36:9: 'ULL'
			{
			match("ULL"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__42"

	// $ANTLR start "T__43"
	public final void mT__43() throws RecognitionException {
		try {
			int _type = T__43;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// CMinus.g:37:7: ( 'Ul' )
			// CMinus.g:37:9: 'Ul'
			{
			match("Ul"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__43"

	// $ANTLR start "T__44"
	public final void mT__44() throws RecognitionException {
		try {
			int _type = T__44;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// CMinus.g:38:7: ( 'Ull' )
			// CMinus.g:38:9: 'Ull'
			{
			match("Ull"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__44"

	// $ANTLR start "T__45"
	public final void mT__45() throws RecognitionException {
		try {
			int _type = T__45;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// CMinus.g:39:7: ( '[' )
			// CMinus.g:39:9: '['
			{
			match('['); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__45"

	// $ANTLR start "T__46"
	public final void mT__46() throws RecognitionException {
		try {
			int _type = T__46;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// CMinus.g:40:7: ( '[]' )
			// CMinus.g:40:9: '[]'
			{
			match("[]"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__46"

	// $ANTLR start "T__47"
	public final void mT__47() throws RecognitionException {
		try {
			int _type = T__47;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// CMinus.g:41:7: ( '[][]' )
			// CMinus.g:41:9: '[][]'
			{
			match("[][]"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__47"

	// $ANTLR start "T__48"
	public final void mT__48() throws RecognitionException {
		try {
			int _type = T__48;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// CMinus.g:42:7: ( ']' )
			// CMinus.g:42:9: ']'
			{
			match(']'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__48"

	// $ANTLR start "T__49"
	public final void mT__49() throws RecognitionException {
		try {
			int _type = T__49;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// CMinus.g:43:7: ( '^' )
			// CMinus.g:43:9: '^'
			{
			match('^'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__49"

	// $ANTLR start "T__50"
	public final void mT__50() throws RecognitionException {
		try {
			int _type = T__50;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// CMinus.g:44:7: ( 'abs' )
			// CMinus.g:44:9: 'abs'
			{
			match("abs"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__50"

	// $ANTLR start "T__51"
	public final void mT__51() throws RecognitionException {
		try {
			int _type = T__51;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// CMinus.g:45:7: ( 'ceil' )
			// CMinus.g:45:9: 'ceil'
			{
			match("ceil"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__51"

	// $ANTLR start "T__52"
	public final void mT__52() throws RecognitionException {
		try {
			int _type = T__52;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// CMinus.g:46:7: ( 'char' )
			// CMinus.g:46:9: 'char'
			{
			match("char"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__52"

	// $ANTLR start "T__53"
	public final void mT__53() throws RecognitionException {
		try {
			int _type = T__53;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// CMinus.g:47:7: ( 'cos' )
			// CMinus.g:47:9: 'cos'
			{
			match("cos"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__53"

	// $ANTLR start "T__54"
	public final void mT__54() throws RecognitionException {
		try {
			int _type = T__54;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// CMinus.g:48:7: ( 'cosh' )
			// CMinus.g:48:9: 'cosh'
			{
			match("cosh"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__54"

	// $ANTLR start "T__55"
	public final void mT__55() throws RecognitionException {
		try {
			int _type = T__55;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// CMinus.g:49:7: ( 'double' )
			// CMinus.g:49:9: 'double'
			{
			match("double"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__55"

	// $ANTLR start "T__56"
	public final void mT__56() throws RecognitionException {
		try {
			int _type = T__56;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// CMinus.g:50:7: ( 'else' )
			// CMinus.g:50:9: 'else'
			{
			match("else"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__56"

	// $ANTLR start "T__57"
	public final void mT__57() throws RecognitionException {
		try {
			int _type = T__57;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// CMinus.g:51:7: ( 'exp' )
			// CMinus.g:51:9: 'exp'
			{
			match("exp"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__57"

	// $ANTLR start "T__58"
	public final void mT__58() throws RecognitionException {
		try {
			int _type = T__58;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// CMinus.g:52:7: ( 'f' )
			// CMinus.g:52:9: 'f'
			{
			match('f'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__58"

	// $ANTLR start "T__59"
	public final void mT__59() throws RecognitionException {
		try {
			int _type = T__59;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// CMinus.g:53:7: ( 'false' )
			// CMinus.g:53:9: 'false'
			{
			match("false"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__59"

	// $ANTLR start "T__60"
	public final void mT__60() throws RecognitionException {
		try {
			int _type = T__60;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// CMinus.g:54:7: ( 'float' )
			// CMinus.g:54:9: 'float'
			{
			match("float"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__60"

	// $ANTLR start "T__61"
	public final void mT__61() throws RecognitionException {
		try {
			int _type = T__61;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// CMinus.g:55:7: ( 'floor' )
			// CMinus.g:55:9: 'floor'
			{
			match("floor"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__61"

	// $ANTLR start "T__62"
	public final void mT__62() throws RecognitionException {
		try {
			int _type = T__62;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// CMinus.g:56:7: ( 'for' )
			// CMinus.g:56:9: 'for'
			{
			match("for"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__62"

	// $ANTLR start "T__63"
	public final void mT__63() throws RecognitionException {
		try {
			int _type = T__63;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// CMinus.g:57:7: ( 'if' )
			// CMinus.g:57:9: 'if'
			{
			match("if"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__63"

	// $ANTLR start "T__64"
	public final void mT__64() throws RecognitionException {
		try {
			int _type = T__64;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// CMinus.g:58:7: ( 'int' )
			// CMinus.g:58:9: 'int'
			{
			match("int"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__64"

	// $ANTLR start "T__65"
	public final void mT__65() throws RecognitionException {
		try {
			int _type = T__65;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// CMinus.g:59:7: ( 'l' )
			// CMinus.g:59:9: 'l'
			{
			match('l'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__65"

	// $ANTLR start "T__66"
	public final void mT__66() throws RecognitionException {
		try {
			int _type = T__66;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// CMinus.g:60:7: ( 'lU' )
			// CMinus.g:60:9: 'lU'
			{
			match("lU"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__66"

	// $ANTLR start "T__67"
	public final void mT__67() throws RecognitionException {
		try {
			int _type = T__67;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// CMinus.g:61:7: ( 'll' )
			// CMinus.g:61:9: 'll'
			{
			match("ll"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__67"

	// $ANTLR start "T__68"
	public final void mT__68() throws RecognitionException {
		try {
			int _type = T__68;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// CMinus.g:62:7: ( 'llU' )
			// CMinus.g:62:9: 'llU'
			{
			match("llU"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__68"

	// $ANTLR start "T__69"
	public final void mT__69() throws RecognitionException {
		try {
			int _type = T__69;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// CMinus.g:63:7: ( 'llu' )
			// CMinus.g:63:9: 'llu'
			{
			match("llu"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__69"

	// $ANTLR start "T__70"
	public final void mT__70() throws RecognitionException {
		try {
			int _type = T__70;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// CMinus.g:64:7: ( 'log' )
			// CMinus.g:64:9: 'log'
			{
			match("log"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__70"

	// $ANTLR start "T__71"
	public final void mT__71() throws RecognitionException {
		try {
			int _type = T__71;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// CMinus.g:65:7: ( 'log10' )
			// CMinus.g:65:9: 'log10'
			{
			match("log10"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__71"

	// $ANTLR start "T__72"
	public final void mT__72() throws RecognitionException {
		try {
			int _type = T__72;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// CMinus.g:66:7: ( 'long' )
			// CMinus.g:66:9: 'long'
			{
			match("long"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__72"

	// $ANTLR start "T__73"
	public final void mT__73() throws RecognitionException {
		try {
			int _type = T__73;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// CMinus.g:67:7: ( 'lu' )
			// CMinus.g:67:9: 'lu'
			{
			match("lu"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__73"

	// $ANTLR start "T__74"
	public final void mT__74() throws RecognitionException {
		try {
			int _type = T__74;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// CMinus.g:68:7: ( 'pow' )
			// CMinus.g:68:9: 'pow'
			{
			match("pow"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__74"

	// $ANTLR start "T__75"
	public final void mT__75() throws RecognitionException {
		try {
			int _type = T__75;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// CMinus.g:69:7: ( 'return' )
			// CMinus.g:69:9: 'return'
			{
			match("return"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__75"

	// $ANTLR start "T__76"
	public final void mT__76() throws RecognitionException {
		try {
			int _type = T__76;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// CMinus.g:70:7: ( 'round' )
			// CMinus.g:70:9: 'round'
			{
			match("round"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__76"

	// $ANTLR start "T__77"
	public final void mT__77() throws RecognitionException {
		try {
			int _type = T__77;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// CMinus.g:71:7: ( 'sin' )
			// CMinus.g:71:9: 'sin'
			{
			match("sin"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__77"

	// $ANTLR start "T__78"
	public final void mT__78() throws RecognitionException {
		try {
			int _type = T__78;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// CMinus.g:72:7: ( 'sinh' )
			// CMinus.g:72:9: 'sinh'
			{
			match("sinh"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__78"

	// $ANTLR start "T__79"
	public final void mT__79() throws RecognitionException {
		try {
			int _type = T__79;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// CMinus.g:73:7: ( 'sqrt' )
			// CMinus.g:73:9: 'sqrt'
			{
			match("sqrt"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__79"

	// $ANTLR start "T__80"
	public final void mT__80() throws RecognitionException {
		try {
			int _type = T__80;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// CMinus.g:74:7: ( 'tan' )
			// CMinus.g:74:9: 'tan'
			{
			match("tan"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__80"

	// $ANTLR start "T__81"
	public final void mT__81() throws RecognitionException {
		try {
			int _type = T__81;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// CMinus.g:75:7: ( 'tanh' )
			// CMinus.g:75:9: 'tanh'
			{
			match("tanh"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__81"

	// $ANTLR start "T__82"
	public final void mT__82() throws RecognitionException {
		try {
			int _type = T__82;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// CMinus.g:76:7: ( 'true' )
			// CMinus.g:76:9: 'true'
			{
			match("true"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__82"

	// $ANTLR start "T__83"
	public final void mT__83() throws RecognitionException {
		try {
			int _type = T__83;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// CMinus.g:77:7: ( 'u' )
			// CMinus.g:77:9: 'u'
			{
			match('u'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__83"

	// $ANTLR start "T__84"
	public final void mT__84() throws RecognitionException {
		try {
			int _type = T__84;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// CMinus.g:78:7: ( 'uL' )
			// CMinus.g:78:9: 'uL'
			{
			match("uL"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__84"

	// $ANTLR start "T__85"
	public final void mT__85() throws RecognitionException {
		try {
			int _type = T__85;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// CMinus.g:79:7: ( 'uLL' )
			// CMinus.g:79:9: 'uLL'
			{
			match("uLL"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__85"

	// $ANTLR start "T__86"
	public final void mT__86() throws RecognitionException {
		try {
			int _type = T__86;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// CMinus.g:80:7: ( 'ul' )
			// CMinus.g:80:9: 'ul'
			{
			match("ul"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__86"

	// $ANTLR start "T__87"
	public final void mT__87() throws RecognitionException {
		try {
			int _type = T__87;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// CMinus.g:81:7: ( 'ull' )
			// CMinus.g:81:9: 'ull'
			{
			match("ull"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__87"

	// $ANTLR start "T__88"
	public final void mT__88() throws RecognitionException {
		try {
			int _type = T__88;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// CMinus.g:82:7: ( 'unsigned' )
			// CMinus.g:82:9: 'unsigned'
			{
			match("unsigned"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__88"

	// $ANTLR start "T__89"
	public final void mT__89() throws RecognitionException {
		try {
			int _type = T__89;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// CMinus.g:83:7: ( '{' )
			// CMinus.g:83:9: '{'
			{
			match('{'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__89"

	// $ANTLR start "T__90"
	public final void mT__90() throws RecognitionException {
		try {
			int _type = T__90;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// CMinus.g:84:7: ( '|' )
			// CMinus.g:84:9: '|'
			{
			match('|'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__90"

	// $ANTLR start "T__91"
	public final void mT__91() throws RecognitionException {
		try {
			int _type = T__91;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// CMinus.g:85:7: ( '||' )
			// CMinus.g:85:9: '||'
			{
			match("||"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__91"

	// $ANTLR start "T__92"
	public final void mT__92() throws RecognitionException {
		try {
			int _type = T__92;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// CMinus.g:86:7: ( '}' )
			// CMinus.g:86:9: '}'
			{
			match('}'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__92"

	// $ANTLR start "ID"
	public final void mID() throws RecognitionException {
		try {
			int _type = ID;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// CMinus.g:286:5: ( ( 'a' .. 'z' | 'A' .. 'Z' | '_' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' )* )
			// CMinus.g:286:9: ( 'a' .. 'z' | 'A' .. 'Z' | '_' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' )*
			{
			if ( (input.LA(1) >= 'A' && input.LA(1) <= 'Z')||input.LA(1)=='_'||(input.LA(1) >= 'a' && input.LA(1) <= 'z') ) {
				input.consume();
			}
			else {
				MismatchedSetException mse = new MismatchedSetException(null,input);
				recover(mse);
				throw mse;
			}
			// CMinus.g:286:33: ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' )*
			loop1:
			while (true) {
				int alt1=2;
				int LA1_0 = input.LA(1);
				if ( ((LA1_0 >= '0' && LA1_0 <= '9')||(LA1_0 >= 'A' && LA1_0 <= 'Z')||LA1_0=='_'||(LA1_0 >= 'a' && LA1_0 <= 'z')) ) {
					alt1=1;
				}

				switch (alt1) {
				case 1 :
					// CMinus.g:
					{
					if ( (input.LA(1) >= '0' && input.LA(1) <= '9')||(input.LA(1) >= 'A' && input.LA(1) <= 'Z')||input.LA(1)=='_'||(input.LA(1) >= 'a' && input.LA(1) <= 'z') ) {
						input.consume();
					}
					else {
						MismatchedSetException mse = new MismatchedSetException(null,input);
						recover(mse);
						throw mse;
					}
					}
					break;

				default :
					break loop1;
				}
			}

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "ID"

	// $ANTLR start "INT"
	public final void mINT() throws RecognitionException {
		try {
			int _type = INT;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// CMinus.g:289:5: ( ( '0' .. '9' )+ )
			// CMinus.g:289:7: ( '0' .. '9' )+
			{
			// CMinus.g:289:7: ( '0' .. '9' )+
			int cnt2=0;
			loop2:
			while (true) {
				int alt2=2;
				int LA2_0 = input.LA(1);
				if ( ((LA2_0 >= '0' && LA2_0 <= '9')) ) {
					alt2=1;
				}

				switch (alt2) {
				case 1 :
					// CMinus.g:
					{
					if ( (input.LA(1) >= '0' && input.LA(1) <= '9') ) {
						input.consume();
					}
					else {
						MismatchedSetException mse = new MismatchedSetException(null,input);
						recover(mse);
						throw mse;
					}
					}
					break;

				default :
					if ( cnt2 >= 1 ) break loop2;
					EarlyExitException eee = new EarlyExitException(2, input);
					throw eee;
				}
				cnt2++;
			}

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "INT"

	// $ANTLR start "FP"
	public final void mFP() throws RecognitionException {
		try {
			int _type = FP;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// CMinus.g:292:5: ( ( '0' .. '9' )* ( '.' ) ( '0' .. '9' )+ ( ( 'e' | 'E' ) ( '-' )? ( '0' .. '9' )+ )? )
			// CMinus.g:292:9: ( '0' .. '9' )* ( '.' ) ( '0' .. '9' )+ ( ( 'e' | 'E' ) ( '-' )? ( '0' .. '9' )+ )?
			{
			// CMinus.g:292:9: ( '0' .. '9' )*
			loop3:
			while (true) {
				int alt3=2;
				int LA3_0 = input.LA(1);
				if ( ((LA3_0 >= '0' && LA3_0 <= '9')) ) {
					alt3=1;
				}

				switch (alt3) {
				case 1 :
					// CMinus.g:
					{
					if ( (input.LA(1) >= '0' && input.LA(1) <= '9') ) {
						input.consume();
					}
					else {
						MismatchedSetException mse = new MismatchedSetException(null,input);
						recover(mse);
						throw mse;
					}
					}
					break;

				default :
					break loop3;
				}
			}

			// CMinus.g:292:21: ( '.' )
			// CMinus.g:292:22: '.'
			{
			match('.'); 
			}

			// CMinus.g:292:27: ( '0' .. '9' )+
			int cnt4=0;
			loop4:
			while (true) {
				int alt4=2;
				int LA4_0 = input.LA(1);
				if ( ((LA4_0 >= '0' && LA4_0 <= '9')) ) {
					alt4=1;
				}

				switch (alt4) {
				case 1 :
					// CMinus.g:
					{
					if ( (input.LA(1) >= '0' && input.LA(1) <= '9') ) {
						input.consume();
					}
					else {
						MismatchedSetException mse = new MismatchedSetException(null,input);
						recover(mse);
						throw mse;
					}
					}
					break;

				default :
					if ( cnt4 >= 1 ) break loop4;
					EarlyExitException eee = new EarlyExitException(4, input);
					throw eee;
				}
				cnt4++;
			}

			// CMinus.g:292:39: ( ( 'e' | 'E' ) ( '-' )? ( '0' .. '9' )+ )?
			int alt7=2;
			int LA7_0 = input.LA(1);
			if ( (LA7_0=='E'||LA7_0=='e') ) {
				alt7=1;
			}
			switch (alt7) {
				case 1 :
					// CMinus.g:292:40: ( 'e' | 'E' ) ( '-' )? ( '0' .. '9' )+
					{
					if ( input.LA(1)=='E'||input.LA(1)=='e' ) {
						input.consume();
					}
					else {
						MismatchedSetException mse = new MismatchedSetException(null,input);
						recover(mse);
						throw mse;
					}
					// CMinus.g:292:50: ( '-' )?
					int alt5=2;
					int LA5_0 = input.LA(1);
					if ( (LA5_0=='-') ) {
						alt5=1;
					}
					switch (alt5) {
						case 1 :
							// CMinus.g:292:51: '-'
							{
							match('-'); 
							}
							break;

					}

					// CMinus.g:292:57: ( '0' .. '9' )+
					int cnt6=0;
					loop6:
					while (true) {
						int alt6=2;
						int LA6_0 = input.LA(1);
						if ( ((LA6_0 >= '0' && LA6_0 <= '9')) ) {
							alt6=1;
						}

						switch (alt6) {
						case 1 :
							// CMinus.g:
							{
							if ( (input.LA(1) >= '0' && input.LA(1) <= '9') ) {
								input.consume();
							}
							else {
								MismatchedSetException mse = new MismatchedSetException(null,input);
								recover(mse);
								throw mse;
							}
							}
							break;

						default :
							if ( cnt6 >= 1 ) break loop6;
							EarlyExitException eee = new EarlyExitException(6, input);
							throw eee;
						}
						cnt6++;
					}

					}
					break;

			}

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "FP"

	// $ANTLR start "WS"
	public final void mWS() throws RecognitionException {
		try {
			int _type = WS;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// CMinus.g:295:5: ( ( ' ' | '\\t' | '\\r' | '\\n' )+ )
			// CMinus.g:295:9: ( ' ' | '\\t' | '\\r' | '\\n' )+
			{
			// CMinus.g:295:9: ( ' ' | '\\t' | '\\r' | '\\n' )+
			int cnt8=0;
			loop8:
			while (true) {
				int alt8=2;
				int LA8_0 = input.LA(1);
				if ( ((LA8_0 >= '\t' && LA8_0 <= '\n')||LA8_0=='\r'||LA8_0==' ') ) {
					alt8=1;
				}

				switch (alt8) {
				case 1 :
					// CMinus.g:
					{
					if ( (input.LA(1) >= '\t' && input.LA(1) <= '\n')||input.LA(1)=='\r'||input.LA(1)==' ' ) {
						input.consume();
					}
					else {
						MismatchedSetException mse = new MismatchedSetException(null,input);
						recover(mse);
						throw mse;
					}
					}
					break;

				default :
					if ( cnt8 >= 1 ) break loop8;
					EarlyExitException eee = new EarlyExitException(8, input);
					throw eee;
				}
				cnt8++;
			}

			_channel=99;
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "WS"

	@Override
	public void mTokens() throws RecognitionException {
		// CMinus.g:1:8: ( T__8 | T__9 | T__10 | T__11 | T__12 | T__13 | T__14 | T__15 | T__16 | T__17 | T__18 | T__19 | T__20 | T__21 | T__22 | T__23 | T__24 | T__25 | T__26 | T__27 | T__28 | T__29 | T__30 | T__31 | T__32 | T__33 | T__34 | T__35 | T__36 | T__37 | T__38 | T__39 | T__40 | T__41 | T__42 | T__43 | T__44 | T__45 | T__46 | T__47 | T__48 | T__49 | T__50 | T__51 | T__52 | T__53 | T__54 | T__55 | T__56 | T__57 | T__58 | T__59 | T__60 | T__61 | T__62 | T__63 | T__64 | T__65 | T__66 | T__67 | T__68 | T__69 | T__70 | T__71 | T__72 | T__73 | T__74 | T__75 | T__76 | T__77 | T__78 | T__79 | T__80 | T__81 | T__82 | T__83 | T__84 | T__85 | T__86 | T__87 | T__88 | T__89 | T__90 | T__91 | T__92 | ID | INT | FP | WS )
		int alt9=89;
		alt9 = dfa9.predict(input);
		switch (alt9) {
			case 1 :
				// CMinus.g:1:10: T__8
				{
				mT__8(); 

				}
				break;
			case 2 :
				// CMinus.g:1:15: T__9
				{
				mT__9(); 

				}
				break;
			case 3 :
				// CMinus.g:1:20: T__10
				{
				mT__10(); 

				}
				break;
			case 4 :
				// CMinus.g:1:26: T__11
				{
				mT__11(); 

				}
				break;
			case 5 :
				// CMinus.g:1:32: T__12
				{
				mT__12(); 

				}
				break;
			case 6 :
				// CMinus.g:1:38: T__13
				{
				mT__13(); 

				}
				break;
			case 7 :
				// CMinus.g:1:44: T__14
				{
				mT__14(); 

				}
				break;
			case 8 :
				// CMinus.g:1:50: T__15
				{
				mT__15(); 

				}
				break;
			case 9 :
				// CMinus.g:1:56: T__16
				{
				mT__16(); 

				}
				break;
			case 10 :
				// CMinus.g:1:62: T__17
				{
				mT__17(); 

				}
				break;
			case 11 :
				// CMinus.g:1:68: T__18
				{
				mT__18(); 

				}
				break;
			case 12 :
				// CMinus.g:1:74: T__19
				{
				mT__19(); 

				}
				break;
			case 13 :
				// CMinus.g:1:80: T__20
				{
				mT__20(); 

				}
				break;
			case 14 :
				// CMinus.g:1:86: T__21
				{
				mT__21(); 

				}
				break;
			case 15 :
				// CMinus.g:1:92: T__22
				{
				mT__22(); 

				}
				break;
			case 16 :
				// CMinus.g:1:98: T__23
				{
				mT__23(); 

				}
				break;
			case 17 :
				// CMinus.g:1:104: T__24
				{
				mT__24(); 

				}
				break;
			case 18 :
				// CMinus.g:1:110: T__25
				{
				mT__25(); 

				}
				break;
			case 19 :
				// CMinus.g:1:116: T__26
				{
				mT__26(); 

				}
				break;
			case 20 :
				// CMinus.g:1:122: T__27
				{
				mT__27(); 

				}
				break;
			case 21 :
				// CMinus.g:1:128: T__28
				{
				mT__28(); 

				}
				break;
			case 22 :
				// CMinus.g:1:134: T__29
				{
				mT__29(); 

				}
				break;
			case 23 :
				// CMinus.g:1:140: T__30
				{
				mT__30(); 

				}
				break;
			case 24 :
				// CMinus.g:1:146: T__31
				{
				mT__31(); 

				}
				break;
			case 25 :
				// CMinus.g:1:152: T__32
				{
				mT__32(); 

				}
				break;
			case 26 :
				// CMinus.g:1:158: T__33
				{
				mT__33(); 

				}
				break;
			case 27 :
				// CMinus.g:1:164: T__34
				{
				mT__34(); 

				}
				break;
			case 28 :
				// CMinus.g:1:170: T__35
				{
				mT__35(); 

				}
				break;
			case 29 :
				// CMinus.g:1:176: T__36
				{
				mT__36(); 

				}
				break;
			case 30 :
				// CMinus.g:1:182: T__37
				{
				mT__37(); 

				}
				break;
			case 31 :
				// CMinus.g:1:188: T__38
				{
				mT__38(); 

				}
				break;
			case 32 :
				// CMinus.g:1:194: T__39
				{
				mT__39(); 

				}
				break;
			case 33 :
				// CMinus.g:1:200: T__40
				{
				mT__40(); 

				}
				break;
			case 34 :
				// CMinus.g:1:206: T__41
				{
				mT__41(); 

				}
				break;
			case 35 :
				// CMinus.g:1:212: T__42
				{
				mT__42(); 

				}
				break;
			case 36 :
				// CMinus.g:1:218: T__43
				{
				mT__43(); 

				}
				break;
			case 37 :
				// CMinus.g:1:224: T__44
				{
				mT__44(); 

				}
				break;
			case 38 :
				// CMinus.g:1:230: T__45
				{
				mT__45(); 

				}
				break;
			case 39 :
				// CMinus.g:1:236: T__46
				{
				mT__46(); 

				}
				break;
			case 40 :
				// CMinus.g:1:242: T__47
				{
				mT__47(); 

				}
				break;
			case 41 :
				// CMinus.g:1:248: T__48
				{
				mT__48(); 

				}
				break;
			case 42 :
				// CMinus.g:1:254: T__49
				{
				mT__49(); 

				}
				break;
			case 43 :
				// CMinus.g:1:260: T__50
				{
				mT__50(); 

				}
				break;
			case 44 :
				// CMinus.g:1:266: T__51
				{
				mT__51(); 

				}
				break;
			case 45 :
				// CMinus.g:1:272: T__52
				{
				mT__52(); 

				}
				break;
			case 46 :
				// CMinus.g:1:278: T__53
				{
				mT__53(); 

				}
				break;
			case 47 :
				// CMinus.g:1:284: T__54
				{
				mT__54(); 

				}
				break;
			case 48 :
				// CMinus.g:1:290: T__55
				{
				mT__55(); 

				}
				break;
			case 49 :
				// CMinus.g:1:296: T__56
				{
				mT__56(); 

				}
				break;
			case 50 :
				// CMinus.g:1:302: T__57
				{
				mT__57(); 

				}
				break;
			case 51 :
				// CMinus.g:1:308: T__58
				{
				mT__58(); 

				}
				break;
			case 52 :
				// CMinus.g:1:314: T__59
				{
				mT__59(); 

				}
				break;
			case 53 :
				// CMinus.g:1:320: T__60
				{
				mT__60(); 

				}
				break;
			case 54 :
				// CMinus.g:1:326: T__61
				{
				mT__61(); 

				}
				break;
			case 55 :
				// CMinus.g:1:332: T__62
				{
				mT__62(); 

				}
				break;
			case 56 :
				// CMinus.g:1:338: T__63
				{
				mT__63(); 

				}
				break;
			case 57 :
				// CMinus.g:1:344: T__64
				{
				mT__64(); 

				}
				break;
			case 58 :
				// CMinus.g:1:350: T__65
				{
				mT__65(); 

				}
				break;
			case 59 :
				// CMinus.g:1:356: T__66
				{
				mT__66(); 

				}
				break;
			case 60 :
				// CMinus.g:1:362: T__67
				{
				mT__67(); 

				}
				break;
			case 61 :
				// CMinus.g:1:368: T__68
				{
				mT__68(); 

				}
				break;
			case 62 :
				// CMinus.g:1:374: T__69
				{
				mT__69(); 

				}
				break;
			case 63 :
				// CMinus.g:1:380: T__70
				{
				mT__70(); 

				}
				break;
			case 64 :
				// CMinus.g:1:386: T__71
				{
				mT__71(); 

				}
				break;
			case 65 :
				// CMinus.g:1:392: T__72
				{
				mT__72(); 

				}
				break;
			case 66 :
				// CMinus.g:1:398: T__73
				{
				mT__73(); 

				}
				break;
			case 67 :
				// CMinus.g:1:404: T__74
				{
				mT__74(); 

				}
				break;
			case 68 :
				// CMinus.g:1:410: T__75
				{
				mT__75(); 

				}
				break;
			case 69 :
				// CMinus.g:1:416: T__76
				{
				mT__76(); 

				}
				break;
			case 70 :
				// CMinus.g:1:422: T__77
				{
				mT__77(); 

				}
				break;
			case 71 :
				// CMinus.g:1:428: T__78
				{
				mT__78(); 

				}
				break;
			case 72 :
				// CMinus.g:1:434: T__79
				{
				mT__79(); 

				}
				break;
			case 73 :
				// CMinus.g:1:440: T__80
				{
				mT__80(); 

				}
				break;
			case 74 :
				// CMinus.g:1:446: T__81
				{
				mT__81(); 

				}
				break;
			case 75 :
				// CMinus.g:1:452: T__82
				{
				mT__82(); 

				}
				break;
			case 76 :
				// CMinus.g:1:458: T__83
				{
				mT__83(); 

				}
				break;
			case 77 :
				// CMinus.g:1:464: T__84
				{
				mT__84(); 

				}
				break;
			case 78 :
				// CMinus.g:1:470: T__85
				{
				mT__85(); 

				}
				break;
			case 79 :
				// CMinus.g:1:476: T__86
				{
				mT__86(); 

				}
				break;
			case 80 :
				// CMinus.g:1:482: T__87
				{
				mT__87(); 

				}
				break;
			case 81 :
				// CMinus.g:1:488: T__88
				{
				mT__88(); 

				}
				break;
			case 82 :
				// CMinus.g:1:494: T__89
				{
				mT__89(); 

				}
				break;
			case 83 :
				// CMinus.g:1:500: T__90
				{
				mT__90(); 

				}
				break;
			case 84 :
				// CMinus.g:1:506: T__91
				{
				mT__91(); 

				}
				break;
			case 85 :
				// CMinus.g:1:512: T__92
				{
				mT__92(); 

				}
				break;
			case 86 :
				// CMinus.g:1:518: ID
				{
				mID(); 

				}
				break;
			case 87 :
				// CMinus.g:1:521: INT
				{
				mINT(); 

				}
				break;
			case 88 :
				// CMinus.g:1:525: FP
				{
				mFP(); 

				}
				break;
			case 89 :
				// CMinus.g:1:528: WS
				{
				mWS(); 

				}
				break;

		}
	}


	protected DFA9 dfa9 = new DFA9(this);
	static final String DFA9_eotS =
		"\2\uffff\1\50\2\uffff\1\52\1\55\1\uffff\1\60\1\62\1\uffff\1\65\1\67\1"+
		"\72\1\73\1\77\1\102\1\104\2\uffff\4\43\1\117\1\43\1\126\4\43\1\141\1\uffff"+
		"\1\143\2\uffff\1\144\27\uffff\1\147\1\150\1\151\1\uffff\1\153\1\155\1"+
		"\uffff\1\157\1\uffff\12\43\1\uffff\1\172\1\43\1\174\1\177\1\43\1\u0082"+
		"\1\uffff\7\43\1\u008b\1\u008d\1\43\4\uffff\1\u008f\1\u0090\3\uffff\1\u0091"+
		"\1\uffff\1\u0092\3\uffff\1\u0093\2\43\1\u0097\2\43\1\u009a\2\43\1\u009e"+
		"\1\uffff\1\u009f\1\uffff\1\u00a0\1\u00a1\1\uffff\1\u00a3\1\43\1\uffff"+
		"\1\u00a5\2\43\1\u00a9\1\43\1\u00ac\1\43\1\u00ae\1\uffff\1\u00af\1\uffff"+
		"\1\43\5\uffff\1\u00b1\1\u00b2\1\u00b3\1\uffff\1\43\1\u00b5\1\uffff\3\43"+
		"\4\uffff\1\43\1\uffff\1\u00ba\1\uffff\2\43\1\u00bd\1\uffff\1\u00be\1\u00bf"+
		"\1\uffff\1\u00c0\2\uffff\1\43\3\uffff\1\43\1\uffff\1\u00c3\1\u00c4\1\u00c5"+
		"\1\u00c6\1\uffff\1\43\1\u00c8\4\uffff\1\43\1\u00ca\4\uffff\1\u00cb\1\uffff"+
		"\1\43\2\uffff\1\43\1\u00ce\1\uffff";
	static final String DFA9_eofS =
		"\u00cf\uffff";
	static final String DFA9_minS =
		"\1\11\1\uffff\1\46\2\uffff\1\75\1\53\1\uffff\1\55\1\75\1\uffff\1\74\2"+
		"\75\3\60\1\135\2\uffff\1\142\1\145\1\157\1\154\1\60\1\146\1\60\1\157\1"+
		"\145\1\151\1\141\1\60\1\uffff\1\174\2\uffff\1\56\27\uffff\3\60\1\uffff"+
		"\2\60\1\uffff\1\133\1\uffff\1\163\1\151\1\141\1\163\1\165\1\163\1\160"+
		"\1\154\1\157\1\162\1\uffff\1\60\1\164\2\60\1\147\1\60\1\uffff\1\167\1"+
		"\164\1\165\1\156\1\162\1\156\1\165\2\60\1\163\4\uffff\2\60\3\uffff\1\60"+
		"\1\uffff\1\60\3\uffff\1\60\1\154\1\162\1\60\1\142\1\145\1\60\1\163\1\141"+
		"\1\60\1\uffff\1\60\1\uffff\2\60\1\uffff\1\60\1\147\1\uffff\1\60\1\165"+
		"\1\156\1\60\1\164\1\60\1\145\1\60\1\uffff\1\60\1\uffff\1\151\5\uffff\3"+
		"\60\1\uffff\1\154\1\60\1\uffff\1\145\1\164\1\162\4\uffff\1\60\1\uffff"+
		"\1\60\1\uffff\1\162\1\144\1\60\1\uffff\2\60\1\uffff\1\60\2\uffff\1\147"+
		"\3\uffff\1\145\1\uffff\4\60\1\uffff\1\156\1\60\4\uffff\1\156\1\60\4\uffff"+
		"\1\60\1\uffff\1\145\2\uffff\1\144\1\60\1\uffff";
	static final String DFA9_maxS =
		"\1\175\1\uffff\1\46\2\uffff\2\75\1\uffff\2\75\1\uffff\2\75\1\76\3\172"+
		"\1\135\2\uffff\1\142\2\157\1\170\1\172\1\156\1\172\2\157\1\161\1\162\1"+
		"\172\1\uffff\1\174\2\uffff\1\71\27\uffff\3\172\1\uffff\2\172\1\uffff\1"+
		"\133\1\uffff\1\163\1\151\1\141\1\163\1\165\1\163\1\160\1\154\1\157\1\162"+
		"\1\uffff\1\172\1\164\2\172\1\156\1\172\1\uffff\1\167\1\164\1\165\1\156"+
		"\1\162\1\156\1\165\2\172\1\163\4\uffff\2\172\3\uffff\1\172\1\uffff\1\172"+
		"\3\uffff\1\172\1\154\1\162\1\172\1\142\1\145\1\172\1\163\1\157\1\172\1"+
		"\uffff\1\172\1\uffff\2\172\1\uffff\1\172\1\147\1\uffff\1\172\1\165\1\156"+
		"\1\172\1\164\1\172\1\145\1\172\1\uffff\1\172\1\uffff\1\151\5\uffff\3\172"+
		"\1\uffff\1\154\1\172\1\uffff\1\145\1\164\1\162\4\uffff\1\60\1\uffff\1"+
		"\172\1\uffff\1\162\1\144\1\172\1\uffff\2\172\1\uffff\1\172\2\uffff\1\147"+
		"\3\uffff\1\145\1\uffff\4\172\1\uffff\1\156\1\172\4\uffff\1\156\1\172\4"+
		"\uffff\1\172\1\uffff\1\145\2\uffff\1\144\1\172\1\uffff";
	static final String DFA9_acceptS =
		"\1\uffff\1\1\1\uffff\1\4\1\5\2\uffff\1\13\2\uffff\1\21\7\uffff\1\51\1"+
		"\52\14\uffff\1\122\1\uffff\1\125\1\126\1\uffff\1\130\1\131\1\2\1\3\1\7"+
		"\1\6\1\11\1\12\1\10\1\15\1\16\1\14\1\20\1\17\1\23\1\24\1\22\1\26\1\25"+
		"\1\30\1\31\1\27\1\32\3\uffff\1\33\2\uffff\1\41\1\uffff\1\46\12\uffff\1"+
		"\63\6\uffff\1\72\12\uffff\1\114\1\124\1\123\1\127\2\uffff\1\34\1\37\1"+
		"\40\1\uffff\1\42\1\uffff\1\44\1\50\1\47\12\uffff\1\70\1\uffff\1\73\2\uffff"+
		"\1\74\2\uffff\1\102\10\uffff\1\115\1\uffff\1\117\1\uffff\1\35\1\36\1\43"+
		"\1\45\1\53\3\uffff\1\56\2\uffff\1\62\3\uffff\1\67\1\71\1\75\1\76\1\uffff"+
		"\1\77\1\uffff\1\103\3\uffff\1\106\2\uffff\1\111\1\uffff\1\116\1\120\1"+
		"\uffff\1\54\1\55\1\57\1\uffff\1\61\4\uffff\1\101\2\uffff\1\107\1\110\1"+
		"\112\1\113\2\uffff\1\64\1\65\1\66\1\100\1\uffff\1\105\1\uffff\1\60\1\104"+
		"\2\uffff\1\121";
	static final String DFA9_specialS =
		"\u00cf\uffff}>";
	static final String[] DFA9_transitionS = {
			"\2\46\2\uffff\1\46\22\uffff\1\46\1\1\4\uffff\1\2\1\uffff\1\3\1\4\1\5"+
			"\1\6\1\7\1\10\1\45\1\11\12\44\1\uffff\1\12\1\13\1\14\1\15\2\uffff\5\43"+
			"\1\16\5\43\1\17\10\43\1\20\5\43\1\21\1\uffff\1\22\1\23\1\43\1\uffff\1"+
			"\24\1\43\1\25\1\26\1\27\1\30\2\43\1\31\2\43\1\32\3\43\1\33\1\43\1\34"+
			"\1\35\1\36\1\37\5\43\1\40\1\41\1\42",
			"",
			"\1\47",
			"",
			"",
			"\1\51",
			"\1\53\21\uffff\1\54",
			"",
			"\1\56\17\uffff\1\57",
			"\1\61",
			"",
			"\1\63\1\64",
			"\1\66",
			"\1\70\1\71",
			"\12\43\7\uffff\32\43\4\uffff\1\43\1\uffff\32\43",
			"\12\43\7\uffff\13\43\1\74\10\43\1\75\5\43\4\uffff\1\43\1\uffff\24\43"+
			"\1\76\5\43",
			"\12\43\7\uffff\13\43\1\100\16\43\4\uffff\1\43\1\uffff\13\43\1\101\16"+
			"\43",
			"\1\103",
			"",
			"",
			"\1\105",
			"\1\106\2\uffff\1\107\6\uffff\1\110",
			"\1\111",
			"\1\112\13\uffff\1\113",
			"\12\43\7\uffff\32\43\4\uffff\1\43\1\uffff\1\114\12\43\1\115\2\43\1\116"+
			"\13\43",
			"\1\120\7\uffff\1\121",
			"\12\43\7\uffff\24\43\1\122\5\43\4\uffff\1\43\1\uffff\13\43\1\123\2\43"+
			"\1\124\5\43\1\125\5\43",
			"\1\127",
			"\1\130\11\uffff\1\131",
			"\1\132\7\uffff\1\133",
			"\1\134\20\uffff\1\135",
			"\12\43\7\uffff\13\43\1\136\16\43\4\uffff\1\43\1\uffff\13\43\1\137\1"+
			"\43\1\140\14\43",
			"",
			"\1\142",
			"",
			"",
			"\1\45\1\uffff\12\44",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"\12\43\7\uffff\24\43\1\145\5\43\4\uffff\1\43\1\uffff\24\43\1\146\5\43",
			"\12\43\7\uffff\32\43\4\uffff\1\43\1\uffff\32\43",
			"\12\43\7\uffff\32\43\4\uffff\1\43\1\uffff\32\43",
			"",
			"\12\43\7\uffff\13\43\1\152\16\43\4\uffff\1\43\1\uffff\32\43",
			"\12\43\7\uffff\32\43\4\uffff\1\43\1\uffff\13\43\1\154\16\43",
			"",
			"\1\156",
			"",
			"\1\160",
			"\1\161",
			"\1\162",
			"\1\163",
			"\1\164",
			"\1\165",
			"\1\166",
			"\1\167",
			"\1\170",
			"\1\171",
			"",
			"\12\43\7\uffff\32\43\4\uffff\1\43\1\uffff\32\43",
			"\1\173",
			"\12\43\7\uffff\32\43\4\uffff\1\43\1\uffff\32\43",
			"\12\43\7\uffff\24\43\1\175\5\43\4\uffff\1\43\1\uffff\24\43\1\176\5\43",
			"\1\u0080\6\uffff\1\u0081",
			"\12\43\7\uffff\32\43\4\uffff\1\43\1\uffff\32\43",
			"",
			"\1\u0083",
			"\1\u0084",
			"\1\u0085",
			"\1\u0086",
			"\1\u0087",
			"\1\u0088",
			"\1\u0089",
			"\12\43\7\uffff\13\43\1\u008a\16\43\4\uffff\1\43\1\uffff\32\43",
			"\12\43\7\uffff\32\43\4\uffff\1\43\1\uffff\13\43\1\u008c\16\43",
			"\1\u008e",
			"",
			"",
			"",
			"",
			"\12\43\7\uffff\32\43\4\uffff\1\43\1\uffff\32\43",
			"\12\43\7\uffff\32\43\4\uffff\1\43\1\uffff\32\43",
			"",
			"",
			"",
			"\12\43\7\uffff\32\43\4\uffff\1\43\1\uffff\32\43",
			"",
			"\12\43\7\uffff\32\43\4\uffff\1\43\1\uffff\32\43",
			"",
			"",
			"",
			"\12\43\7\uffff\32\43\4\uffff\1\43\1\uffff\32\43",
			"\1\u0094",
			"\1\u0095",
			"\12\43\7\uffff\32\43\4\uffff\1\43\1\uffff\7\43\1\u0096\22\43",
			"\1\u0098",
			"\1\u0099",
			"\12\43\7\uffff\32\43\4\uffff\1\43\1\uffff\32\43",
			"\1\u009b",
			"\1\u009c\15\uffff\1\u009d",
			"\12\43\7\uffff\32\43\4\uffff\1\43\1\uffff\32\43",
			"",
			"\12\43\7\uffff\32\43\4\uffff\1\43\1\uffff\32\43",
			"",
			"\12\43\7\uffff\32\43\4\uffff\1\43\1\uffff\32\43",
			"\12\43\7\uffff\32\43\4\uffff\1\43\1\uffff\32\43",
			"",
			"\1\43\1\u00a2\10\43\7\uffff\32\43\4\uffff\1\43\1\uffff\32\43",
			"\1\u00a4",
			"",
			"\12\43\7\uffff\32\43\4\uffff\1\43\1\uffff\32\43",
			"\1\u00a6",
			"\1\u00a7",
			"\12\43\7\uffff\32\43\4\uffff\1\43\1\uffff\7\43\1\u00a8\22\43",
			"\1\u00aa",
			"\12\43\7\uffff\32\43\4\uffff\1\43\1\uffff\7\43\1\u00ab\22\43",
			"\1\u00ad",
			"\12\43\7\uffff\32\43\4\uffff\1\43\1\uffff\32\43",
			"",
			"\12\43\7\uffff\32\43\4\uffff\1\43\1\uffff\32\43",
			"",
			"\1\u00b0",
			"",
			"",
			"",
			"",
			"",
			"\12\43\7\uffff\32\43\4\uffff\1\43\1\uffff\32\43",
			"\12\43\7\uffff\32\43\4\uffff\1\43\1\uffff\32\43",
			"\12\43\7\uffff\32\43\4\uffff\1\43\1\uffff\32\43",
			"",
			"\1\u00b4",
			"\12\43\7\uffff\32\43\4\uffff\1\43\1\uffff\32\43",
			"",
			"\1\u00b6",
			"\1\u00b7",
			"\1\u00b8",
			"",
			"",
			"",
			"",
			"\1\u00b9",
			"",
			"\12\43\7\uffff\32\43\4\uffff\1\43\1\uffff\32\43",
			"",
			"\1\u00bb",
			"\1\u00bc",
			"\12\43\7\uffff\32\43\4\uffff\1\43\1\uffff\32\43",
			"",
			"\12\43\7\uffff\32\43\4\uffff\1\43\1\uffff\32\43",
			"\12\43\7\uffff\32\43\4\uffff\1\43\1\uffff\32\43",
			"",
			"\12\43\7\uffff\32\43\4\uffff\1\43\1\uffff\32\43",
			"",
			"",
			"\1\u00c1",
			"",
			"",
			"",
			"\1\u00c2",
			"",
			"\12\43\7\uffff\32\43\4\uffff\1\43\1\uffff\32\43",
			"\12\43\7\uffff\32\43\4\uffff\1\43\1\uffff\32\43",
			"\12\43\7\uffff\32\43\4\uffff\1\43\1\uffff\32\43",
			"\12\43\7\uffff\32\43\4\uffff\1\43\1\uffff\32\43",
			"",
			"\1\u00c7",
			"\12\43\7\uffff\32\43\4\uffff\1\43\1\uffff\32\43",
			"",
			"",
			"",
			"",
			"\1\u00c9",
			"\12\43\7\uffff\32\43\4\uffff\1\43\1\uffff\32\43",
			"",
			"",
			"",
			"",
			"\12\43\7\uffff\32\43\4\uffff\1\43\1\uffff\32\43",
			"",
			"\1\u00cc",
			"",
			"",
			"\1\u00cd",
			"\12\43\7\uffff\32\43\4\uffff\1\43\1\uffff\32\43",
			""
	};

	static final short[] DFA9_eot = DFA.unpackEncodedString(DFA9_eotS);
	static final short[] DFA9_eof = DFA.unpackEncodedString(DFA9_eofS);
	static final char[] DFA9_min = DFA.unpackEncodedStringToUnsignedChars(DFA9_minS);
	static final char[] DFA9_max = DFA.unpackEncodedStringToUnsignedChars(DFA9_maxS);
	static final short[] DFA9_accept = DFA.unpackEncodedString(DFA9_acceptS);
	static final short[] DFA9_special = DFA.unpackEncodedString(DFA9_specialS);
	static final short[][] DFA9_transition;

	static {
		int numStates = DFA9_transitionS.length;
		DFA9_transition = new short[numStates][];
		for (int i=0; i<numStates; i++) {
			DFA9_transition[i] = DFA.unpackEncodedString(DFA9_transitionS[i]);
		}
	}

	protected class DFA9 extends DFA {

		public DFA9(BaseRecognizer recognizer) {
			this.recognizer = recognizer;
			this.decisionNumber = 9;
			this.eot = DFA9_eot;
			this.eof = DFA9_eof;
			this.min = DFA9_min;
			this.max = DFA9_max;
			this.accept = DFA9_accept;
			this.special = DFA9_special;
			this.transition = DFA9_transition;
		}
		@Override
		public String getDescription() {
			return "1:1: Tokens : ( T__8 | T__9 | T__10 | T__11 | T__12 | T__13 | T__14 | T__15 | T__16 | T__17 | T__18 | T__19 | T__20 | T__21 | T__22 | T__23 | T__24 | T__25 | T__26 | T__27 | T__28 | T__29 | T__30 | T__31 | T__32 | T__33 | T__34 | T__35 | T__36 | T__37 | T__38 | T__39 | T__40 | T__41 | T__42 | T__43 | T__44 | T__45 | T__46 | T__47 | T__48 | T__49 | T__50 | T__51 | T__52 | T__53 | T__54 | T__55 | T__56 | T__57 | T__58 | T__59 | T__60 | T__61 | T__62 | T__63 | T__64 | T__65 | T__66 | T__67 | T__68 | T__69 | T__70 | T__71 | T__72 | T__73 | T__74 | T__75 | T__76 | T__77 | T__78 | T__79 | T__80 | T__81 | T__82 | T__83 | T__84 | T__85 | T__86 | T__87 | T__88 | T__89 | T__90 | T__91 | T__92 | ID | INT | FP | WS );";
		}
	}

}
