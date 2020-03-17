package com.juliencomtois.evaluator;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.juliencomtois.evaluator.Evaluator;
import com.juliencomtois.evaluator.exception.InvalidEquationException;

@RunWith(Parameterized.class)
public class EvaluatorExceptionTest {

	private final static String ERROR_MESSAGE_OPERATORS = "Two operators next to eachother";
	private final static String ERROR_MESSAGE_OPERANDS = "Two operands next to eachother";
	private final static String ERROR_MESSAGE_PARENTHASES = "Parenthases don't match up";
	private final static String ERROR_MESSAGE_EMPTY_PARENTHASES = "Parenthases are empty";
	private final static String ERROR_MESSAGE_MISSING_OPERAND = "An operand is missing";
	private final static String ERROR_MESSAGE_DIVISION_BY_ZERO = "Division by zero";
	
	private final Logger log = LoggerFactory.getLogger(getClass().getName());
	
	private Evaluator evaluator;
	private String expectedMessage;
	private String testMsg;
	private static Queue<String> infix;
	
	@Parameters(name = "Case{index}: {0} = {1}")
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] { 
			{ "3 + ( ) + 3", ERROR_MESSAGE_EMPTY_PARENTHASES, "Test empty parenthases" }, 
			{ "3 + + 3", ERROR_MESSAGE_OPERATORS, "Test double operators" }, 
			{ ") 3 + 3 (", ERROR_MESSAGE_PARENTHASES, "Test closing parenthasis before opening" }, 
			{ "( ) 3 + 3", ERROR_MESSAGE_EMPTY_PARENTHASES, "Test empty parenthases at beggining" }, 
			{ "3 + 3 ( )", ERROR_MESSAGE_EMPTY_PARENTHASES, "Test empty parenthases at end" }, 
			{ "( ( 3 + 3 ) + 3", ERROR_MESSAGE_PARENTHASES, "Test more opening than closing parenthases" }, 
			{ "( 3 + 3 ) + 3 )", ERROR_MESSAGE_PARENTHASES, "Test more closing than opening parenthases" }, 
			{ "3 3 + 3", ERROR_MESSAGE_OPERANDS, "Test missing operator" },
			{ "+ 3 + 3", ERROR_MESSAGE_MISSING_OPERAND, "Test missing operand" },
			{ "7 / 0", ERROR_MESSAGE_DIVISION_BY_ZERO, "Test division by zero" },
			{ "7 / ( 3 - 3 )", ERROR_MESSAGE_DIVISION_BY_ZERO, "Test division by zero(result)" }
		});
	}
	
	public EvaluatorExceptionTest(String expression, String expectedMessage, String testMsg) {
		this.expectedMessage = expectedMessage;
		this.testMsg = testMsg;

		String[] array = expression.split(" ");
		infix = new LinkedList<String>(Arrays.asList(array));
	}

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Test
	public void testEvaluatorException() throws InvalidEquationException {
		log.info(testMsg);
		expectedException.expect(InvalidEquationException.class);
		expectedException.expectMessage(expectedMessage);
		evaluator = new Evaluator();
		evaluator.calculate(infix);
	}
}