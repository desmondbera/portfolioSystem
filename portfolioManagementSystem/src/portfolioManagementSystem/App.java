package portfolioManagementSystem;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class App {

	private static float cash = 0;
	// Example: {"aapl"=2} - meaning user has 2 shares of "appl" stocks
	private static HashMap<String, Integer> userHoldings = new HashMap<String, Integer>();
	// Example: {"0430"=155.00} - meaning on day "0430" user had a portfolio value
	// of $155
	private static HashMap<String, Float> userDailyPortfolioValues = new HashMap<String, Float>();
	private static int dayCounter = 1;

	public enum StockNames {
		MSFT, AAPL, FB, AMZN, GOOG
	}

	public static void main(String[] args) {
//		showCsvFile();
		Scanner sc = new Scanner(System.in);

		String currentMonth;
		String currentDay;

		boolean exitFlag = false;
		boolean initialDepositFlag = false;
		while (dayCounter < 25 && !exitFlag) {

			currentMonth = getMonthWithCounter(dayCounter);
			currentDay = getDayWithCounter(dayCounter);

			System.out.println("|| WELCOME, TODAY'S DATE IS: " + currentMonth + "/" + currentDay + " ||");

			// 1. Ask user what option they want
			System.out.println("Enter 1 to start buying / selling stocks: ");

			System.out.println("Enter 2 to go to tomorrow: ");

			System.out.println("Enter 3 to caclulate returns: ");

			System.out.println("Enter 4 to exit program.");

			String userChoice = sc.next();
			System.out.println("You chose option: " + userChoice);

			switch (userChoice) {

			case ("1"):

				boolean keepTradingTodayFlag = true;
				while (keepTradingTodayFlag) {
					if (dayCounter == 1 || !initialDepositFlag) {
						System.out.println("How much cash did you want to deposit into your portfolio? ");
						cash = sc.nextFloat();
						System.out.printf("You've deposited %f\n", cash);
						initialDepositFlag = true;
					}

					// 2. Ask user whether they want to buy OR sell

					System.out.println("Enter 'buy' to buy or 'sell' to sell: ");
					String userAction = sc.next();
					System.out.println("You've entered: " + userAction);

					// 3. Show user ALL STOCKS that are available on THIS DAY
					System.out.println("Enter the name of the stock you want to " + userAction + ": ");

					for (StockNames s : StockNames.values()) {
						System.out.println("- " + s);
					}

					// 4. Confirm the price of X-STOCK AND the amount of shares to buy:
					String userPickedStock = sc.next();

					// 4.1 Check if stock exists
					Boolean doesStockExist = checkIfStockExists(userPickedStock);
					while (!doesStockExist) {
						System.out.println("That stock does not exist. Try another stock name: ");
						for (StockNames s : StockNames.values()) {
							System.out.println(s);
						}

						userPickedStock = sc.next();
						doesStockExist = checkIfStockExists(userPickedStock);

					}
					// 4.2 Check how much the stock is trading current day

					System.out.println("You picked " + userPickedStock + ".");

					String stockPrice = currStockPickedPrice(userPickedStock, currentMonth, currentDay);
					System.out.println("It's currently trading at " + stockPrice + " per share");

					// 4.3 Ask user many shares of X-Stock they want to buy

					System.out
							.println("How many shares of " + userPickedStock + " did you want to " + userAction + "?");
					int stockCount = sc.nextInt();

					String action = userAction.equalsIgnoreCase("buy") ? "purchase" : "sale";

					System.out.println("Please hold. Confirming " + action + " of " + stockCount + " shares of "
							+ userPickedStock);

					// 5. Update userHoldings

					boolean validHoldingsUpdate = updateHoldings(userPickedStock, stockPrice, stockCount, userAction);
					// System.out.println("validHoldingsUpdate is: " + validHoldingsUpdate);

					boolean exitOption = false;
					while (!validHoldingsUpdate && !exitOption) {
						System.out.println("-------------------------------------------------");
						System.out.println("**You don't have enough to " + userAction + "!**");
						System.out.println("-------------------------------------------------");
						System.out.println("Enter 1 to try to buy a different stock: ");
						System.out.println("Enter 2 to sell your stocks: ");
						System.out.println("Enter 3 to go to the next day: ");
						String userDeniedTransaction = sc.next();
						switch (userDeniedTransaction) {
						case ("1"):
							userAction = "buy";
							System.out.println("Enter a stock name again: ");

							for (StockNames s : StockNames.values()) {
								System.out.println("-" + s);
							}

							userPickedStock = sc.next();

							// Validation to check stock name exists in our system
							Boolean doesStockExist2 = checkIfStockExists(userPickedStock);
							while (!doesStockExist2) {
								System.out.println("That stock does not exist. Try another stock name: ");
								for (StockNames s : StockNames.values()) {
									System.out.println(s);
								}
								userPickedStock = sc.next();
								doesStockExist2 = checkIfStockExists(userPickedStock);
							}
							stockPrice = currStockPickedPrice(userPickedStock, currentMonth, currentDay);
							System.out.println("How many shares of " + userPickedStock + " did you want to buy?");
							stockCount = sc.nextInt();

							validHoldingsUpdate = updateHoldings(userPickedStock, stockPrice, stockCount, userAction);

							break;
						case ("2"):
							userAction = "sell";

							System.out.println("Available for sale from your portfolio: ");
							showUserHoldings(currentMonth, currentDay);

							// 1. if userHolding is not empty then we can ask the user which stocks they
							// want to sell

							boolean exitFlag2 = false;
							if (!userHoldings.isEmpty()) {

								// 1.2 We need to loop thru until the correct stock name is picked:
								System.out.println("Enter one of the stocks in your portfolio to sell: ");
								String userPickedStockToSell = sc.next();
								boolean doesStockExistInHoldings = checkIfStockExistsInHoldings(userPickedStockToSell);
								System.out.println("doesStockExistInHoldings is: " + doesStockExistInHoldings);
								// TODO: make validation loop to keep asking if the answer is wrong

								while (!(doesStockExistInHoldings || exitFlag2)) {
									System.out.println("That stock does not exist. Enter a stock that exists in your holdings OR 'x' to exit: ");
									userPickedStockToSell = sc.next();
									System.out.println("******our userPickedStockToSell is: " + userPickedStockToSell);
									System.out.println("Our doesStockExistInHoldings is: " + doesStockExistInHoldings);
									System.out.println("our exitFlag2 is: " + exitFlag2);
									if (userPickedStockToSell.toString().contentEquals("x")) {
										System.out.println("USER INPUTTED X!");
										exitFlag2 = true;
										break;
									} else {
										doesStockExistInHoldings = checkIfStockExistsInHoldings(userPickedStockToSell);
									}

								}

								// if exitFlag still false then we can ask for the # of shares to sell
								if (!exitFlag2) {
									// 1.3 We need to loop thru until the correct amount is picked (<= to current
									// amount)
									System.out.println("Enter the amount you want to sell: ");
									int userStockCountToSell = sc.nextInt();
									boolean doesStockCountExistInHoldings = checkIfStockCountExistsInHoldings(userStockCountToSell);
									System.out.println("doesStockCountExistInHoldings is: " + doesStockCountExistInHoldings);
									// TODO: make validation loop to keep asking if the answer is wrong
									while(!(doesStockCountExistInHoldings || exitFlag2)) {
										System.out.println("You don't have that many shares of this stock. Try another amount OR '0' to exit: ");
										userStockCountToSell = sc.nextInt();
										System.out.println("Our userStockCountToSell is: " + userStockCountToSell);
										if(userStockCountToSell == 0) {
											exitFlag2 = true;
											break;
										} else {
											doesStockCountExistInHoldings = checkIfStockCountExistsInHoldings(userStockCountToSell);
										}
										
									}
									// 1.4 Once a vuserPickedStockToSellalid stock name AND amount has been chosen
									// then we can update our userHoldings
									
									//if exitFlag has been tripped by the count validation then we DON'T update 
									if(!exitFlag2) {
										stockPrice = currStockPickedPrice(userPickedStockToSell, currentMonth, currentDay);
										validHoldingsUpdate = updateHoldings(userPickedStockToSell, stockPrice,
												userStockCountToSell, userAction);
										System.out.println("Our validHoldingsUpdate is: " + validHoldingsUpdate);
									}
									
								}

							}

							break;
						case ("3"):
							System.out.println("Bye bye!");
							exitOption = true;
							break;
						default:
							System.out.println("That's not a valid option.");
							break;

						}

					}

					// 6. Show userHoldings after transaction
					if (validHoldingsUpdate) {
						updateUserDailyPortfolioValues(currentMonth, currentDay);
						showUserHoldings(currentMonth, currentDay);
						showUserCash();

					}
					// CHECKING to see if our userDailyPortfolioValues was updated!
//					System.out.println("###########################");
//					
//					System.out.println("Our userDailyPortfolioValues is: " + userDailyPortfolioValues);
//					
//					System.out.println("###########################");

					// 7. Give user option to go to the next day (MAY MOVE THIS UP EARLIER)
					System.out.println("Enter 'Y' to go to the next day or 'N' to keep trading today: ");
					String userMoveForward = sc.next();

					if (userMoveForward.equalsIgnoreCase("y")) {
						keepTradingTodayFlag = false;
						// 1. We need to keep a counter somewhere above
						// 2. Increment counter by 1 if user wants to move on
						dayCounter++;

						// 3. Using the counter we keep track which row we are supposed to be on
						currentMonth = getMonthWithCounter(dayCounter);
						currentDay = getDayWithCounter(dayCounter);
						// 4. BE CAREFUL - ROW 0 DOES NOT HAVE THE NUMBERS - only column names
					} else {
						dayCounter++;
					}

				}

				break;
			case ("2"):
				// TODO:
//				System.out.println("Inside 2nd case!");
				if (userHoldings.isEmpty()) {
					userDailyPortfolioValues.put(currentMonth + currentDay, (float) 0);
				}

				dayCounter++;
				break;
			case ("3"):
				System.out.println("Calculating portfolio returns...");
				System.out.println("Checking what our userHoldings is in CALCULATIONS: " + userHoldings);
				System.out
						.println("Checking what our userDailyPortfolioValues is in CALCS: " + userDailyPortfolioValues);

				// 1. we need to ask the user what day to start with AND what day to end with
				// for calculation
				System.out.println("Enter start month for calculations (Ex: 04): ");
				String startMonth = sc.next();
				System.out.println("Enter start day for calculations (Ex: 02): ");
				String startDay = sc.next();

				// 1.1 Is user Input for startMonth + startDay BEFORE our current month +
				// current day?
				boolean startDateBeforeCurr = isStartDateBeforeCurrent(startMonth, startDay, currentMonth, currentDay);

				// 1.2 Is there a KEY that matches our start date in userDailyPortfolioValues?
				boolean startDateInUserDailyMap = isDateInUserDailyPortfolio(startMonth, startDay);

				// 1.2 Is this start date the user picked in the CSV?
				boolean startDateInCSV = isDateInCSV(startMonth, startDay);

				System.out.println("---------");
				System.out.println("Our result for startDateBeforeCurr is: " + startDateBeforeCurr);
				System.out.println("Our result for startDateInUserDailyMap: " + startDateInUserDailyMap);
				System.out.println("Our result for startDateInCSv: " + startDateInCSV);
				System.out.println("---------");

				// TODO: FIX: this code will be reached when user enter '3' instead of '03' -
				// even tho it's a valid date
				while (!(startDateInCSV && startDateBeforeCurr && startDateInUserDailyMap)) {

					System.out.println("It's not a valid START MONTH and / or START DAY");

					System.out.println("Enter a new START MONTH for calculations (Ex: 04): ");
					startMonth = sc.next();
					System.out.println("Enter a new START DAY for calculations (Ex: 07): ");
					startDay = sc.next();

					startDateBeforeCurr = isStartDateBeforeCurrent(startMonth, startDay, currentMonth, currentDay);
					startDateInUserDailyMap = isDateInUserDailyPortfolio(startMonth, startDay);
					startDateInCSV = isDateInCSV(startMonth, startDay);
				}

				System.out.println("==========================================");

				// 2. The END DATE the user wants to check run calculations on
				System.out.println("Enter end month for calculations (Ex: 05): ");
				String endMonth = sc.next();
				System.out.println("Enter end day for calculations (Ex: 22): ");
				String endDay = sc.next();

				// 2.1 Is the user submitted END DATE *AFTER* user submitted START DATE
				boolean endDateAfterStart = isEndDateAfterStartDate(endMonth, endDay, startMonth, startDay);

				// 2.2 Is there a KEY that matches our END DATE in userDailyPortfolioValues?
				boolean endDateInUserDailyMap = isDateInUserDailyPortfolio(endMonth, endDay);

				// 2.3 Is this END DATE the user picked in the CSV?
				boolean endDateInCSV = isDateInCSV(endMonth, endDay);

				System.out.println("---------");
				System.out.println("Our result for endDateAfterStart is: " + endDateAfterStart);
				System.out.println("Our result for endDateInUserDailyMap: " + endDateInUserDailyMap);
				System.out.println("Our result for endDateInCSV: " + endDateInCSV);
				System.out.println("---------");

				while (!(endDateAfterStart && endDateInUserDailyMap && endDateInCSV)) {
					System.out.println("It's not a valid END MONTH and / or END DAY");

					System.out.println("Enter a new END MONTH for calculations (Ex: 04): ");
					endMonth = sc.next();
					System.out.println("Enter a new END DAY for calculations (Ex: 07): ");
					endDay = sc.next();

					endDateAfterStart = isEndDateAfterStartDate(endMonth, endDay, startMonth, startDay);
					endDateInUserDailyMap = isDateInUserDailyPortfolio(endMonth, endDay);
					endDateInCSV = isDateInCSV(endMonth, endDay);
				}

				System.out.println("-------------------------");
				System.out.println("----PORTFOLIO RETURNS----");
				System.out.println("FROM: " + startMonth + "/" + startDay);
				System.out.println("TO: " + endMonth + "/" + endDay);
				System.out.println(
						"Percentage (%): " + getPortfolioReturnsOverPeriod(startMonth, startDay, endMonth, endDay));

				System.out.println("-------------------------");

				// 3. then given those days we calculate the percent change in PORTFOLIO VALUE
				// 4. in order to calculate across multiple days we need to keep track of the
				// portfolio value for each day
				// Perhaps as a HashMap. Key = date, value = daily portofolio value

				break;
			case ("4"):
				System.out.println("BYE BYE!");
				exitFlag = true;
				break;
			default:
				System.out.println("That is not a valid input.");
				break;
			}

		}
		sc.close();

//			System.out.println("Outside of the BIG SWITCH CASE");
//			System.out.println("Userholdings: " + userHoldings);
	}

	private static boolean checkIfStockCountExistsInHoldings(int userStockCountToSell) {
		boolean result = false;

		for (Map.Entry<String, Integer> entry : userHoldings.entrySet()) {
			if (userStockCountToSell <= entry.getValue()) {
				result = true;
			}
		}

		return result;
	}

	private static boolean checkIfStockExistsInHoldings(String userStockToSell) {
		boolean result = false;

		for (Map.Entry<String, Integer> entry : userHoldings.entrySet()) {
			if (entry.getKey().equalsIgnoreCase(userStockToSell)) {
				result = true;
			}
		}

		return result;
	}

	private static String getPortfolioReturnsOverPeriod(String startMonth, String startDay, String endMonth,
			String endDay) {

		// 1. we need to get the difference between END DATE VALUE and START DATE VALUE
		float endDateDailyValue = getUserDailyPortfolioValueByMonthAndDay(endMonth, endDay);
		float startDateDailyValue = getUserDailyPortfolioValueByMonthAndDay(startMonth, startDay);

		// 2. divide that difference between by START DATE VALUE
		float diff = endDateDailyValue - startDateDailyValue;
//		System.out.println("#### -> Our difference is: " + diff);

		float ans = diff / startDateDailyValue;
//		System.out.println("Our answer is: " + ans);

		float ansToPercent = ans * 100;
//		System.out.println("Our ansToPercent is: " + ansToPercent);
		// 3. multiply previous answer by 100 to get percentage
		return Float.isNaN(ansToPercent) ? "0" : Float.toString(ansToPercent);

	}

	private static float getUserDailyPortfolioValueByMonthAndDay(String month, String day) {
		float result = 0;
		String dateString = month + day;

		for (Map.Entry<String, Float> entry : userDailyPortfolioValues.entrySet()) {
			if (entry.getKey().contentEquals(dateString)) {
				result = entry.getValue();
			}
		}

		return result;
	}

	private static void updateUserDailyPortfolioValues(String currentMonth, String currentDay) {
		String dateString = currentMonth + currentDay;

		// 1. get value from USERHOLDING using date
		String currPortfolioValue = getUserPortfolioValue(currentMonth, currentDay);

		// 2. put this value into the USERDAILYPORTFOLIOVALUES
		userDailyPortfolioValues.put(dateString, Float.parseFloat(currPortfolioValue));

	}

	private static boolean isEndDateAfterStartDate(String endMonth, String endDay, String startMonth, String startDay) {
		boolean result = false;

		int endMonthToInt = Integer.parseInt(endMonth);
		int startMonthToInt = Integer.parseInt(startMonth);

		int endDayToInt = Integer.parseInt(endDay);
		int startDayToInt = Integer.parseInt(startDay);

		if (endMonthToInt > startMonthToInt) {
//			System.out.println("End month is after Start month");
			result = true;
		}

		if (endMonthToInt == startMonthToInt) {
//			System.out.println("Same month. Checking days now: ");
			if (endDayToInt > startDayToInt) {
				result = true;
			}
		}

		return result;
	}

	private static boolean isDateInUserDailyPortfolio(String startMonth, String startDay) {
		boolean result = false;
		String dateString = startMonth + startDay;

		for (Map.Entry<String, Float> entry : userDailyPortfolioValues.entrySet()) {
			if (entry.getKey().contentEquals(dateString)) {
				result = true;
			}
		}

		return result;
	}

	private static boolean isStartDateBeforeCurrent(String startMonth, String startDay, String currentMonth,
			String currentDay) {
		boolean result = false;

		int startMonthToInt = Integer.parseInt(startMonth);
		int currentMonthToInt = Integer.parseInt(currentMonth);

		int startDayToInt = Integer.parseInt(startDay);
		int currentDayToInt = Integer.parseInt(currentDay);

		// If start month is less than current month set to TRUE
		if (startMonthToInt < currentMonthToInt) {
			System.out.println("Start month is less than current month!");
			result = true;

		}

		// If start month is the same as current month we need to check the DAYS
		if (startMonthToInt == currentMonthToInt) {
			System.out.println("Start month equals current month");
			if (startDayToInt < currentDayToInt) {
				System.out.println("start day is before current day");
				result = true;
			}
		}

		return result;
	}

	private static boolean isDateInCSV(String month, String day) {
		boolean result = false;

		// 1. put month and day together as one string
		String userMonthDayString = month + day;
//		System.out.println("Our monthDayString is: " + userMonthDayString);
		// 2. loop thru the csv file - since it is ordered we might want to do a binary
		// search to make it faster + efficient with a bigger csv file
		String csvFile = "InterviewCodingPrices.csv";
		String line = "";
		String splitBy = ",";

		try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {

//			System.out.println("Inside try!");
			while ((line = br.readLine()) != null) {
				String[] holding = line.split(splitBy);
//				System.out.println("Holding Date: " + holding[0]);
				String currentDate = holding[0];
				String currentMonthDateStr = currentDate.substring(4);
//				System.out.println("currentMonthDateStr is: " + currentMonthDateStr);
//				System.out.println("userMonthDayString is: " + userMonthDayString);
				if (currentMonthDateStr.equalsIgnoreCase(userMonthDayString)) {
//					System.out.println("we found one!");
					result = true;
					break;
				}

			}

		} catch (IOException e) {
			System.out.println("Inside catch!");
			e.printStackTrace();
		}

		return result;
	}

	private static String getDayWithCounter(int dayCounter2) {
		String newDate = getDateWithCounter(dayCounter2);
		String newDay = newDate.substring(6);
		return newDay;
	}

	private static String getMonthWithCounter(int dayCounter2) {
		String newDate = getDateWithCounter(dayCounter2);
		String newFirstMonth = newDate.substring(4, 6);
		return newFirstMonth;
	}

	private static String getDateWithCounter(int dayCounter2) {
		String firstDateFromCSV = null;

		String csvFile = "InterviewCodingPrices.csv";
		String line = "";
		String splitBy = ",";

		try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {

			int count = 0;
			while ((line = br.readLine()) != null) {
				String[] holding = line.split(splitBy);
				if (count == dayCounter2) {
					firstDateFromCSV = holding[0];
					break;
				}
				count++;
			}

		} catch (IOException e) {
			System.out.println("Inside catch!");
			e.printStackTrace();
		}

		return firstDateFromCSV;
	}

	private static void showUserCash() {
		System.out.println("====");
		System.out.println("Cash available: " + cash);
		System.out.println("====");

	}

	private static void showUserHoldings(String currentMonth, String currentDay) {
		System.out.println("======================================================");
		System.out.println("::::::::::HOLDINGS::::::::::");
		String portfolioValue = getUserPortfolioValue(currentMonth, currentDay);
		System.out.println("- PORTFOLIO VALUE: $" + portfolioValue + " - ");

		userHoldings.forEach((k,
				v) -> System.out.println("STOCK NAME: " + k + ". || # OF SHARES: " + v + " || HOLDING VALUE: "
						+ getHoldingValueOfStock(k, v, currentMonth, currentDay) + " || HOLDING WEIGHT: "
						+ getHoldingWeightOfStock(k, v, currentMonth, currentDay, portfolioValue) + "%"
						+ " || CASH WEIGHT: " + getCashWeight(currentMonth, currentDay)));

		System.out.println("======================================================");
	}

	// TODO: re-do this. My assumption is most likely wrong. Cash value should be
	// saved at the time stock was purchased.
	// Then we can use that value to get the correct CASH WEIGHT.
	private static String getCashWeight(String currentMonth, String currentDay) {
//		
//		System.out.println("----=====-----");
//		System.out.println("Inside get Cashweight!");
//		System.out.println("Cash is: " + cash);
//		System.out.println("Our portfolio value is: "+Float.parseFloat(getUserPortfolioValue(currentMonth, currentDay)));
//		System.out.println("----=====-----");
		return Float.toString(cash / Float.parseFloat(getUserPortfolioValue(currentMonth, currentDay)));
	}

	private static String getHoldingWeightOfStock(String k, Integer v, String currentMonth, String currentDay,
			String portfolioValue) {
		// Holding weight = holding value / portfolio value
		float holdingWeight = 100 * Float.parseFloat(getHoldingValueOfStock(k, v, currentMonth, currentDay))
				/ Float.parseFloat(portfolioValue);
		return Float.toString(holdingWeight);
	}

	private static String getHoldingValueOfStock(String k, Integer v, String currentMonth, String currentDay) {
		String priceOfStockX = currStockPickedPrice(k, currentMonth, currentDay);
		float sharesXPrice = v * Float.parseFloat(priceOfStockX);
		return Float.toString(sharesXPrice);
	}

	private static String getUserPortfolioValue(String currentMonth, String currentDay) {
		// Portfolio value = Cash + Sum of all Holding values
//		System.out.println("Our cash is: " + cash);
		float sumOfAllHoldingValues = 0;
		for (Map.Entry<String, Integer> entry : userHoldings.entrySet()) {
			sumOfAllHoldingValues += Float
					.parseFloat(getHoldingValueOfStock(entry.getKey(), entry.getValue(), currentMonth, currentDay));
		}

		return Float.toString(sumOfAllHoldingValues);

	}

	private static boolean updateHoldings(String userPickedStock, String stockPrice, int stockCount,
			String userAction) {
		boolean result = false;

		// 1. check if we are doing BUYING or SELLING
		// 2. if we are buying, we need to make sure we have enough cash.
		// If not enough cash, then we return false and don't update holdings
		if (userAction.equalsIgnoreCase("buy")) {
			float stockPriceFloat = Float.parseFloat(stockPrice);
			float totalCostToBuy = stockPriceFloat * stockCount;
//			System.out.println("Our stockPriceFloat is: " + stockPriceFloat);
//			System.out.println("Our totalCostTobuy is: " + totalCostToBuy);

			if (totalCostToBuy <= cash) {
//				System.out.println("You have enough cash to buy this. WOOHOO.");
				// 2.1 Now we subtract cash by the totalCost to buy the stocks
//				System.out.println("Our cash before adjusting for purchase: " + cash);
				cash -= totalCostToBuy;
//				System.out.println("Our cash is now: " + cash);
				// 2.2 Then we update our holdings map with the STOCKNAME as the KEY and the
				// STOCKCOUNT as the VALUE
				if (userHoldings.containsKey(userPickedStock)) {
					userHoldings.put(userPickedStock, userHoldings.get(userPickedStock) + stockCount);
				} else {
					userHoldings.put(userPickedStock, stockCount);
				}
				result = true;
			}
		}

		// 3. if we are selling then we perform the calculations to get the current
		// value of the stock * the count. Then we update our cash variable.
		if (userAction.equalsIgnoreCase("sell")) {
//			System.out.println("Inside sellingggggg");
//			// **** TEST *****
//
//			userHoldings.put("appl", 2);
//
//			// ***** TEST *****

			// 3.1 check if there is ANYTHING to sell
			// 3.2 if there is NOTHING to sell return false
			if (userHoldings.isEmpty()) {
				System.out.println("Nothing in your holdings to sell!");
			} else {
				// 3.3 else if there is SOMETHING to sell
				// we have to make sure we have the AVAILABLE STOCK COUNT
//				System.out.println("userHoldings.get(userPickedStock) is: " + userHoldings.get(userPickedStock));

				if (stockCount <= userHoldings.get(userPickedStock)) {
//					System.out.println("We have enough to sell!");

					float stockPriceFloat2 = Float.parseFloat(stockPrice);
					float totalAfterStockSale = stockPriceFloat2 * stockCount;
					cash += totalAfterStockSale;
					int totalCountInHoldings = userHoldings.get(userPickedStock);
					totalCountInHoldings -= stockCount;
					userHoldings.put(userPickedStock, totalCountInHoldings);

//					System.out.println("Our new total in userholdings is: " + userHoldings.get(userPickedStock));
//					System.out.println("Our new cash is: " + cash);
					result = true;
				}
			}

		}

		return result;
	}

	private static String currStockPickedPrice(String userPickedStock, String currentMonth, String currentDay) {
		String currentPriceOfStock = null;

		String csvFile = "InterviewCodingPrices.csv";
		String line = "";
		String splitBy = ",";

		try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {

//			System.out.println("Inside try!");
			while ((line = br.readLine()) != null) {
				String[] holding = line.split(splitBy);

				if (!holding[0].toString().contentEquals("date")) {
//					System.out.println(holding[0].substring(4, 6).contains(Integer.toString(currentMonth)));
//					System.out.println(holding[0].substring(6).contains(Integer.toString(currentDay)));
					if (holding[0].substring(4, 6).contains(currentMonth)
							&& holding[0].substring(6).contains(currentDay)) {

						if (userPickedStock.equalsIgnoreCase("msft")) {
//							System.out.println("MSFT price today: " + holding[1]);
							currentPriceOfStock = holding[1];
						} else if (userPickedStock.equalsIgnoreCase("aapl")) {
//							System.out.println("AAPL price today: " + holding[2]);
							currentPriceOfStock = holding[2];
						} else if (userPickedStock.equalsIgnoreCase("fb")) {
//							System.out.println("FB price today: " + holding[3]);
							currentPriceOfStock = holding[3];
						} else if (userPickedStock.equalsIgnoreCase("amzn")) {
//							System.out.println("AMZN price today: " + holding[4]);
							currentPriceOfStock = holding[4];
						} else if (userPickedStock.equalsIgnoreCase("goog")) {
//							System.out.println("GOOG price today: " + holding[5]);
							currentPriceOfStock = holding[5];
						}

					}
				}

			}

		} catch (IOException e) {
			System.out.println("Inside catch!");
			e.printStackTrace();
		}

		return currentPriceOfStock;

	}

	private static Boolean checkIfStockExists(String userPickedStock) {
		boolean result = false;

		for (StockNames s : StockNames.values()) {
			if (s.toString().equalsIgnoreCase(userPickedStock)) {
				result = true;
				break;
			}
		}

		return result;
	}

	public static void showCsvFile() {
		String csvFile = "InterviewCodingPrices.csv";
		String line = "";
		String splitBy = ",";

		try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {

			System.out.println("Inside try!");
			while ((line = br.readLine()) != null) {
				String[] holding = line.split(splitBy);
				System.out.println("Holding Date: " + holding[0]);
				System.out.println("MSFT Share Price: " + holding[1]);
				System.out.println("AAPL share price: " + holding[2]);
				System.out.println("FB share price: " + holding[3]);
				System.out.println("AMZN share price: " + holding[4]);
				System.out.println("GOOG share price: " + holding[5]);
				System.out.println("======");

			}

		} catch (IOException e) {
			System.out.println("Inside catch!");
			e.printStackTrace();
		}
	}

}
