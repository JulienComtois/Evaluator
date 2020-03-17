package com.juliencomtois.evaluator;

import java.math.BigDecimal;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Queue;

import com.juliencomtois.evaluator.exception.InvalidEquationException;

/**
 * The Evaluator class has methods to convert an infix expression to a postfix expression and calculate the result of the postfix expression
 * 
 * @author Julien Comtois - 1020645
 * @version 12/15/2015s
 */
public class Evaluator {
	
	// Error messages used when throwing exceptions
	private final String ERROR_MESSAGE_OPERATORS = "Two operators next to eachother";
	private final String ERROR_MESSAGE_OPERANDS = "Two operands next to eachother";
	private final String ERROR_MESSAGE_PARENTHASES = "Parenthases don't match up";
	private final String ERROR_MESSAGE_EMPTY_PARENTHASES = "Parenthases are empty";
	private final String ERROR_MESSAGE_MISSING_OPERAND = "An operand is missing";
	private final String ERROR_MESSAGE_DIVISION_BY_ZERO = "Division by zero";

	/**
	 * Uses the infixToPostfix and postfixEval methods to calculate the result of an infix expression
	 * 
	 * @param infix A Queue representing an infix expression
	 * @return The result of the calculation
	 * @throws InvalidEquationException Thrown if the infix expression is invalid
	 */
	public BigDecimal calculate(Queue<String> infix) throws InvalidEquationException {
		return postfixEval(infixToPostfix(infix));
	}

	/**
	 * Converts a Queue representing an infix expression to a Queue representing a postfix expression
	 * 
	 * @param infix A Queue representing an infix expression
	 * @return A Queue representing a postfix expression
	 * @throws InvalidEquationException Thrown if the infix expression is invalid
	 */
	public Queue<String> infixToPostfix(Queue<String> infix) throws InvalidEquationException {
		Deque<String> stack = new ArrayDeque<String>();
		Queue<String> postfix = new LinkedList<String>();
		String item;
		boolean previousWasOperator = false;
		boolean previousWasOperand = false;
		boolean previousWasDivision = false;
		boolean previousWasOpenParenthasis = false;
		boolean isFirstIteration = true;
		int openCount = 0;
		int closeCount = 0;

		for (String current : infix) {
			switch (current) {
			case "(":
				openCount++;
				stack.push(current);
				previousWasOpenParenthasis = true;
				break;
			case ")":
				closeCount++;
				if (closeCount > openCount) {
					throw new InvalidEquationException(ERROR_MESSAGE_PARENTHASES);
				}
				if (previousWasOpenParenthasis) {
					throw new InvalidEquationException(ERROR_MESSAGE_EMPTY_PARENTHASES);
				}
				// Pop items off stack and add them to postfix until "(" is found and discarded
				while (!(item = stack.pop()).equals("(")) {
					postfix.add(item);
				}
				previousWasOpenParenthasis = false;
				break;
			case "/":
			case "*":
			case "+":
			case "-":
				// Didn't do this in it's own case because I wouldn't have to copy paste code
				if (current.equals("/")) {
					previousWasDivision = true;
				} else {
					previousWasDivision = false;
				}
				if (isFirstIteration) {
					throw new InvalidEquationException(ERROR_MESSAGE_MISSING_OPERAND);
				}
				if (previousWasOperator) {
					throw new InvalidEquationException(ERROR_MESSAGE_OPERATORS);
				}
				previousWasOperator = true;
				previousWasOperand = false;
				previousWasOpenParenthasis = false;
				if (stack.isEmpty() || stack.peek().equals("(")) {
					stack.push(current);
				} else {
					while (stack.peek() != null && !checkHigherPrecedence(current, stack.peek())) {
						postfix.add(stack.pop());
					}
					stack.push(current);
				}
				break;
			case "0":
				if (previousWasDivision) {
					throw new InvalidEquationException(ERROR_MESSAGE_DIVISION_BY_ZERO);
				}
				// Fall through intended
			default:
				if (previousWasOperand) {
					throw new InvalidEquationException(ERROR_MESSAGE_OPERANDS);
				} else {
					postfix.add(current);
					previousWasOperator = false;
					previousWasOperand = true;
					previousWasOpenParenthasis = false;
					previousWasDivision = false;
				}
			}
			isFirstIteration = false;
		}
		if (closeCount != openCount) {
			throw new InvalidEquationException(ERROR_MESSAGE_PARENTHASES);
		}
		while (stack.peek() != null) {
			postfix.add(stack.pop());
		}
		return postfix;
	}

	/**
	 * Calculates the result of a Queue representing a postfix expression
	 * 
	 * @param postfix A Queue representing a postfix expression
	 * @return The result of the calculation
	 * @throws InvalidEquationException Thrown if the infix expression is invalid
	 */
	public BigDecimal postfixEval(Queue<String> postfix) throws InvalidEquationException {
		BigDecimal operand1;
		BigDecimal operand2;
		Deque<String> stack = new ArrayDeque<String>();
		for (String current : postfix) {
			switch (current) {
			case "/":
				operand1 = new BigDecimal(stack.pop());
				operand2 = new BigDecimal(stack.pop());
				try {
					stack.push(String.valueOf(operand2.divide(operand1, 3, BigDecimal.ROUND_HALF_UP)));
				} catch (ArithmeticException e) {
					throw new InvalidEquationException(ERROR_MESSAGE_DIVISION_BY_ZERO);
				}
				break;
			case "*":
				operand1 = new BigDecimal(stack.pop());
				operand2 = new BigDecimal(stack.pop());
				stack.push(String.valueOf(operand2.multiply(operand1)));
				break;
			case "+":
				operand1 = new BigDecimal(stack.pop());
				operand2 = new BigDecimal(stack.pop());
				stack.push(String.valueOf(operand2.add(operand1)));
				break;
			case "-":
				operand1 = new BigDecimal(stack.pop());
				operand2 = new BigDecimal(stack.pop());
				stack.push(String.valueOf(operand2.subtract(operand1)));
				break;
			default:
				stack.push(current);
			}
		}
		return (new BigDecimal(stack.pop())).stripTrailingZeros();
	}

	/**
	 * Check if one operand is higher precedence than another.
	 * 
	 * @param operand1 First operand
	 * @param operand2 Second operand
	 * @return True if operand1 is higher precedence than oprand2
	 */
	private boolean checkHigherPrecedence(String operand1, String operand2) {
		boolean isMultiplicationOrDivision = operand1.equals("/") || operand1.equals("*");
		boolean isAdditionOrSubtraction = operand2.equals("+") || operand2.equals("-");
		if (isMultiplicationOrDivision && isAdditionOrSubtraction) {
			return true;
		}
		return false;
	}
}