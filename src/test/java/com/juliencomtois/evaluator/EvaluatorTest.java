package com.juliencomtois.evaluator;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.juliencomtois.evaluator.Evaluator;
import com.juliencomtois.evaluator.exception.InvalidEquationException;

@RunWith(Parameterized.class)
public class EvaluatorTest {

	private final Logger log = LoggerFactory.getLogger(getClass().getName());
	private Evaluator evaluator;
	private String expectedResult;
	private String testMsg;
	private Queue<String> infix;
	private Queue<String> postfix;
	
	@Parameters(name = "Case{index}: {0} = {1}")
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] { 
			{ "5 + 8", "5 8 +", "13", "Test 2 operand addition" },
			{ "8 - 5", "8 5 -", "3", "Test 2 operand subtraction" },
			{ "5 - 8", "5 8 -", "-3", "Test 2 operand subtraction with negative result" },
			{ "2 * 4", "2 4 *", "8", "Test 2 operand multiplication" }, 
			{ "10 / 5", "10 5 /", "2", "Test 2 operand division" },
			{ "1 + 2 + 3 + 4 + 5", "1 2 + 3 + 4 + 5 +", "15", "Test several additions" },
			{ "1 - 2 - 3 - 4 - 5", "1 2 - 3 - 4 - 5 -", "-13", "Test several substractions" },
			{ "1 * 2 * 3 * 4 * 5", "1 2 * 3 * 4 * 5 *", "120", "Test several multiplications" },
			{ "100 / 2 / 5 / 2", "100 2 / 5 / 2 /", "5", "Test several divisions" },
			{ "10 / 5 * 2 + 4 - 7", "10 5 / 2 * 4 + 7 -", "1", "Test all operands" },
			{ "1 + 2 * 3 - 4", "1 2 3 * + 4 -", "3", "Test example from site" },
			{ "10 + 5 * 2", "10 5 2 * +", "20", "Test operators of different prededence" }, 
			{ "-10 + -5 * -2", "-10 -5 -2 * +", "0", "Test operators of different prededence with negative numbers" }, 
			{ "435 - 34 * 2", "435 34 2 * -", "367", "Test triple digit numbers" },
			{ "-435 - -34 * 2", "-435 -34 2 * -", "-367", "Test triple digits with negative numbers" }, 
			{ "( 10 + 5 ) * 2", "10 5 + 2 *", "30", "Test parentheses" },
			{ "( 10 + 5 * 2 )", "10 5 2 * +", "20", "Test parentheses at begginning and end" },
			{ "( ( 17 ) + ( 38 ) )", "17 38 +", "55", "Test parentheses within other parenthases" },
			{ "( ( ( 17 ) * ( 17 ) ) + ( 38 ) )", "17 17 * 38 +", "327", "Test parentheses within other parentheses" },
			{ "6 / 2 * 5 + 7 - 3", "6 2 / 5 * 7 + 3 -", "19", "Tet precedence order highest first" },
			{ "20 - 5 + 2 * 2 / 2", "20 5 - 2 2 * 2 / +", "17", "Tet precedence order lowest first" },
			{ "1.6 * 2.12 / 6.3 - 4.67", "1.6 2.12 * 6.3 / 4.67 -", "-4.132", "Test decimals" },
			{ "1.6 * 2.12 / ( 6.3 - 4.67 )", "1.6 2.12 * 6.3 4.67 - /", "2.081", "Test decimals and parenthases" },
			{ "-1.6 * -2.12 / ( -6.3 - -4.67 )", "-1.6 -2.12 * -6.3 -4.67 - /", "-2.081", "Test decimals and parenthases with negative numbers" },
			{ "( ( ( ( ( 56.9 * 0.09 ) ) ) ) ) / ( ( 3 - 4.4 ) - ( 79 - 0.3 ) / ( 3 / 2.4 ) ) / 0.5 - 2",
				"56.9 0.09 * 3 4.4 - 79 0.3 - 3 2.4 / / - / 0.5 / 2 -", "-2.16", "Test madness" }
		});
	}

	public EvaluatorTest(String expression, String expectedPostfix, String expectedResult, String testMsg) {
		evaluator = new Evaluator();
		this.expectedResult = expectedResult;
		this.testMsg = testMsg;

		String[] arrInfix = expression.split(" ");
		infix = new LinkedList<String>(Arrays.asList(arrInfix));
		String[] arrPostfix = expectedPostfix.split(" ");
		postfix = new LinkedList<String>(Arrays.asList(arrPostfix));
	}

	@Test
	public void testInfixToPostfix() throws InvalidEquationException {
		log.info(testMsg);
		assertEquals(postfix, evaluator.infixToPostfix(infix));
	}

	@Test
	public void testPostfixEval() throws InvalidEquationException {
		assertEquals(expectedResult, evaluator.postfixEval(postfix).toPlainString());
	}
}