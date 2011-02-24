package com.xebia.incubator.xebium;

import static org.apache.commons.lang.StringUtils.removeStartIgnoreCase;
import static org.apache.commons.lang.StringUtils.trim;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * This class provides calls to all operations defined in the Selenium IDE.
 * 
 * @author arjan
 *
 */
public class ExtendedSeleniumCommand {

	private static final String GET = "get";

	private static final String IS = "is";

	private static final String STORE = "store";

	private static final String VERIFY = "verify";

	private static final String ASSERT = "assert";

	private static final String WAIT_FOR = "waitFor";
	
	private static final String WAIT_FOR_NOT = "waitForNot";
	
	private static final String NOT_PRESENT = "NotPresent";

	private static final String AND_WAIT = "AndWait";

	// Matching types
	private static final String REGEXP = "regexp:";
	private static final String REGEXPI = "regexpi:";
	private static final String EXACT = "exact:";
	private static final String GLOB = "glob:";



	// keywords copied from org.openqa.selenium.WebDriverCommandProcessor
	private static final Set<String> SELENIUM_COMMANDS = new HashSet<String>(Arrays.asList(new String[] {
		"addLocationStrategy",
		"addScript",
		"addSelection",
		"allowNativeXpath",
		"altKeyDown",
		"altKeyUp",
		"answerOnNextPrompt",
		"assertErrorOnNext",
		"assertFailureOnNext",
		"assertSelected",
		"assignId",
		"attachFile",
		"break",
		"captureScreenshotToString",
		"check",
		"chooseCancelOnNextConfirmation",
		"chooseOkOnNextConfirmation",
		"click",
		"clickAt",
		"close",
		"contextMenu",
		"contextMenuAt",
		"controlKeyDown",
		"controlKeyUp",
		"createCookie",
		"deleteAllVisibleCookies",
		"deleteCookie",
		"deselectPopUp",
		"doubleClick",
		"doubleClickAt",
		"dragAndDrop",
		"dragAndDropToObject",
		"dragdrop",
		"echo",
		"fireEvent",
		"focus",
		"getAlert",
		"getAllButtons",
		"getAllFields",
		"getAllLinks",
		"getAllWindowIds",
		"getAllWindowNames",
		"getAllWindowTitles",
		"getAttribute",
		"getAttributeFromAllWindows",
		"getBodyText",
		"getConfirmation",
		"getCookie",
		"getCookieByName",
		"getCursorPosition",
		"getElementHeight",
		"getElementIndex",
		"getElementPositionLeft",
		"getElementPositionTop",
		"getElementWidth",
		"getEval",
		"getExpression",
		"getHtmlSource",
		"getLocation",
		"getMouseSpeed",
		"getPrompt",
		"getSelectOptions",
		"getSelectedId",
		"getSelectedIds",
		"getSelectedIndex",
		"getSelectedIndexes",
		"getSelectedLabel",
		"getSelectedLabels",
		"getSelectedValue",
		"getSelectedValues",
		"getSpeed",
		"getTable",
		"getText",
		"getTitle",
		"getValue",
		"getWhetherThisFrameMatchFrameExpression",
		"getWhetherThisWindowMatchWindowExpression",
		"getXpathCount",
		"goBack",
		"highlight",
		"ignoreAttributesWithoutValue",
		"isAlertPresent",
		"isChecked",
		"isConfirmationPresent",
		"isCookiePresent",
		"isEditable",
		"isElementPresent",
		"isOrdered",
		"isPromptPresent",
		"isSomethingSelected",
		"isTextPresent",
		"isVisible",
		"keyDown",
		"keyDownNative",
		"keyPress",
		"keyPressNative",
		"keyUp",
		"keyUpNative",
		"metaKeyDown",
		"metaKeyUp",
		"mouseDown",
		"mouseDownAt",
		"mouseDownRight",
		"mouseDownRightAt",
		"mouseMove",
		"mouseMoveAt",
		"mouseOut",
		"mouseOver",
		"mouseUp",
		"mouseUpAt",
		"mouseUpRight",
		"mouseUpRightAt",
		"open",
		"openWindow",
		"pause",
		"refresh",
		"removeAllSelections",
		"removeScript",
		"removeSelection",
		"retrieveLastRemoteControlLogs",
		"rollup",
		"runScript",
		"select",
		"selectFrame",
		"selectPopUp",
		"selectWindow",
		"setBrowserLogLevel",
		"setContext",
		"setCursorPosition",
		"setMouseSpeed",
		"setSpeed",
		"setTimeout",
		"shiftKeyDown",
		"shiftKeyUp",
		"shutDownSeleniumServer",
		"store",
		"submit",
		"type",
		"typeKeys",
		"uncheck",
		"useXpathLibrary",
		"waitForCondition",
		"waitForFrameToLoad",
		"waitForPageToLoad",
		"waitForPopUp",
		"windowFocus",
		"windowMaximize"
	}));

	private String methodName;
	
	public ExtendedSeleniumCommand(String methodName) {
		this.methodName = methodName;
	}

	public boolean isWaitForCommand() {
		return methodName.startsWith(WAIT_FOR);
	}
	
	public boolean isAndWaitCommand() {
		return  methodName.endsWith(AND_WAIT);
	}
	
	// TODO: process this in JS
	public boolean isNegateCommand() {
		return methodName.startsWith(WAIT_FOR_NOT) || methodName.endsWith(NOT_PRESENT);
	}
	
	public boolean isAssertCommand() {
		return methodName.startsWith(ASSERT);
	}
	
	public boolean isVerifyCommand() {
		return methodName.startsWith(VERIFY);
	}
	
	public boolean isStoreCommand() {
		return methodName.startsWith(STORE);
	}
	
	public boolean isCaptureEntirePageScreenshotCommand() {
		return methodName.startsWith("captureEntirePageScreenshot");
	}

	public boolean isBooleanCommand() {
		return getSeleniumCommand().startsWith(IS);
	}
	
	public String getSeleniumCommand() {
		// for commands like "waitForCondition"
		if (SELENIUM_COMMANDS.contains(methodName)) {
			return methodName;
		}
		
		String seleniumName = methodName;
		
		if (isAssertCommand() || isVerifyCommand() || isStoreCommand() || isWaitForCommand()) {
			// ASSERT.length() == VERIFY.length()
			String noun = seleniumName.substring(isStoreCommand() ? STORE.length() :
				(isWaitForCommand() ? WAIT_FOR.length() : ASSERT.length()));
			
			if (isNegateCommand()) {
				//noun = noun.substring(0, noun.length() - NOT_PRESENT.length()) + "Present";
				noun = noun.replaceAll("Not([A-Z])", "$1");
			}
			
			if (SELENIUM_COMMANDS.contains(IS + noun)) {
				seleniumName = IS + noun;
			} else if (SELENIUM_COMMANDS.contains(GET + noun)) {
				seleniumName = GET + noun;
			}
		} else if (isCaptureEntirePageScreenshotCommand()) {
			seleniumName = "captureScreenshotToString";
		}
		
		if (isAndWaitCommand()) {
			seleniumName = seleniumName.substring(0, seleniumName.length() - AND_WAIT.length());
		}
		return seleniumName;
	}

	/**
	 * <p><i>(From the Selenium docs)</i></p>
	 * <p>
	 * Various Pattern syntaxes are available for matching string values:
	 * </p>
	 * <ul>
	 * <li><strong>glob:</strong><em>pattern</em>: Match a string against a
	 * "glob" (aka "wildmat") pattern. "Glob" is a kind of limited
	 * regular-expression syntax typically used in command-line shells. In a
	 * glob pattern, "*" represents any sequence of characters, and "?"
	 * represents any single character. Glob patterns match against the entire
	 * string.</li>
	 * <li><strong>regexp:</strong><em>regexp</em>: Match a string using a
	 * regular-expression. The full power of JavaScript regular-expressions is
	 * available.</li>
	 * <li><strong>regexpi:</strong><em>regexpi</em>: Match a string using a
	 * case-insensitive regular-expression.</li>
	 * <li><strong>exact:</strong><em>string</em>:
	 * 
	 * Match a string exactly, verbatim, without any of that fancy wildcard
	 * stuff.</li>
	 * </ul>
	 * <p>
	 * If no pattern prefix is specified, Selenium assumes that it's a "glob"
	 * pattern.
	 * </p>
	 * <p>
	 * For commands that return multiple values (such as verifySelectOptions),
	 * the string being matched is a comma-separated list of the return values,
	 * where both commas and backslashes in the values are backslash-escaped.
	 * When providing a pattern, the optional matching syntax (i.e. glob,
	 * regexp, etc.) is specified once, as usual, at the beginning of the
	 * pattern.
	 * </p>
	 * 
	 * @param expected expected result, optionally prefixed
	 * @param actual Actual result coming from Selenium
	 * @return is it a match?
	 */
	public boolean matches(String expected, String actual) {
		boolean result;
		if (isBooleanCommand()) {
			result = (isNegateCommand() ? "false" : "true").equals(actual);
			
		} else if (expected.startsWith(REGEXP)) {
			final String regex = trim(removeStartIgnoreCase(expected, REGEXP));
			result = Pattern.compile(regex).matcher(actual).matches();
			
		} else if (expected.startsWith(REGEXPI)) {
			final String regex = trim(removeStartIgnoreCase(expected, REGEXPI));
			result = Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(actual).matches();
			
		} else if (expected.startsWith(EXACT)){
			final String str = trim(removeStartIgnoreCase(expected, EXACT));
			result = str.equals(actual);
			
		} else {
			// "glob:"
			final String pattern;
			if (expected.startsWith(GLOB)) {
				pattern = trim(removeStartIgnoreCase(expected, GLOB));
			} else {
				pattern = expected;
			}
			result = globToRegExp(pattern).matcher(actual).matches();
		}
		return result;
	}

	private Pattern globToRegExp(String pattern) {
		return Pattern.compile(pattern.replaceAll("\\?", ".").replaceAll("\\*", ".*"));
	}

}