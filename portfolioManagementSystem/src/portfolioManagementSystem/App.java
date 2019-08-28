package portfolioManagementSystem;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class App {

	private static float cash = 0;
	private static HashMap<String, Integer> userHoldings = new HashMap<String, Integer>();
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
		while(dayCounter < 25 && !exitFlag) {
			
			currentMonth = getMonthWithCounter(dayCounter);
			currentDay = getDayWithCounter(dayCounter);
			
			System.out.println("|| WELCOME, TODAY'S DATE IS: " + currentMonth + "/" + currentDay + " ||");

			
			// 1. Ask user what option they want
			System.out.println("Enter 1 to start buying / selling stocks: ");

			System.out.println("Enter 2 to go to tomorrow: ");
			
			System.out.println("Enter 3 to exit program.");
			
			String userChoice = sc.next();
			System.out.println("You chose option: " + userChoice);
			
			
			switch (userChoice) {

			case ("1"):
	
				if(dayCounter == 1) {
					System.out.println("How much cash did you want to deposit into your portfolio? ");
					cash = sc.nextFloat();
					System.out.printf("You've deposited %f\n", cash);
				}
				
	
				// 2. Ask user whether they want to buy OR sell
				
				System.out.println("Enter 'buy' to buy or 'sell' to sell: ");
				String userAction = sc.next();
				System.out.println("You've entered: " + userAction);
	
				// 3. Show user ALL STOCKS that are available on THIS DAY
				System.out.println("Enter the name of the stock you want to " + userAction + ": ");
	
				for (StockNames s : StockNames.values()) {
					System.out.println(s);
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
//				System.out.println("Currentmonth here is: " + currentMonth);
//				System.out.println("Current day herre is: " + currentDay);
				
				String stockPrice = currStockPickedPrice(userPickedStock, currentMonth, currentDay);
				System.out.println("It's currently trading at " + stockPrice + " per share");
	
				// 4.3 Ask user many shares of X-Stock they want to buy
	
				System.out.println("How many shares of " + userPickedStock + " did you want to " + userAction + "?");
				int stockCount = sc.nextInt();
	
				String action = userAction.equalsIgnoreCase("buy") ? "purchase" : "sale";
	
				System.out.println("Please hold. Confirming " + action + " of " + stockCount + " shares of " + userPickedStock);
	
				// 5. Update userHoldings 
	
				boolean validHoldingsUpdate = updateHoldings(userPickedStock, stockPrice, stockCount, userAction);
	//			System.out.println("validHoldingsUpdate is: " + validHoldingsUpdate);
	
				boolean exitOption = false;
				while (!validHoldingsUpdate && !exitOption) {
					System.out.println("**You don't have a enough cash!**");
					System.out.println("Enter 1 to try another stock: ");
					System.out.println("Enter 2 to sell your stocks: ");
					System.out.println("Enter 3 to close shop for the day: ");
	
					String userDeniedTransaction = sc.next();
					switch (userDeniedTransaction) {
	
					case ("1"):
						System.out.println("Try another stock: ");
	
						for (StockNames s : StockNames.values()) {
							System.out.println(s);
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
						//TODO: 
						System.out.println("Selling...");
	
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
					showUserHoldings(currentMonth, currentDay);
					showUserCash();
				}
				// 7. Give user option to go to the next day (MAY MOVE THIS UP EARLIER)
				System.out.println("Enter 'Y' if you want to go to the next day: ");
				String userMoveForward = sc.next();
	
				if (userMoveForward.equalsIgnoreCase("y")) {
					// 1. We need to keep a counter somewhere above
					// 2. Increment counter by 1 if user wants to move on
					dayCounter++;
					
					// 3. Using the counter we keep track which row we are supposed to be on
					currentMonth = getMonthWithCounter(dayCounter);
					currentDay = getDayWithCounter(dayCounter);
					// 4. BE CAREFUL - ROW 0 DOES NOT HAVE THE NUMBERS - only column names
				}
				break;
			case ("2"):
				//TODO: 
				System.out.println("Inside 2nd case!");
				dayCounter++;
				break;
			case ("3"):
				System.out.println("BYE BYE!");
				exitFlag = true;
				break;
			default:
				System.out.println("That is not a valid input.");
				break;
			}
			
		}
		
			System.out.println("Outside of the BIG SWITCH CASE");
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
		System.out.println("==================");
		System.out.println("Your holdings are listed below: ");
		String portfolioValue = getUserPortfolioValue(currentMonth, currentDay);
		System.out.println("Portfolio value: " + portfolioValue);
		
		userHoldings.forEach((k, v) -> System.out.println("Stock name: " + k 
				+ ". | # of shares: " + v 
				+ " | Holding Value: " + getHoldingValueOfStock(k, v, currentMonth, currentDay) 
				+ " | Holding Weight: " + getHoldingWeightOfStock(k, v, currentMonth, currentDay, portfolioValue) 
				+ " | Cash Weight: " + getCashWeight(currentMonth, currentDay)));
		
		System.out.println("==================");
	}

	
	//TODO: re-do this. My assumption is most likely wrong. Cash value should be saved at the time stock was purchased. 
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

	private static String getHoldingWeightOfStock(String k, Integer v, String currentMonth, String currentDay, String portfolioValue) {
		// Holding weight = holding value / portfolio value
		float holdingWeight = Float.parseFloat(getHoldingValueOfStock(k, v, currentMonth, currentDay)) / Float.parseFloat(portfolioValue) ;
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
		for(Map.Entry<String, Integer> entry: userHoldings.entrySet()) {
			sumOfAllHoldingValues += Float.parseFloat(getHoldingValueOfStock(entry.getKey(), entry.getValue(), currentMonth, currentDay));
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
//			System.out.println("Inside buyingggggggg");
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
				if(userHoldings.containsKey(userPickedStock)) {
					userHoldings.put(userPickedStock, userHoldings.get(userPickedStock) + stockCount);
				} else {
					userHoldings.put(userPickedStock, stockCount);
				}
//				userHoldings.put(userPickedStock, userHoldings.getOrDefault(userPickedStock, stockCount) + 1);
				result = true;
			}
		}

		// 3. if we are selling then we perform the calculations to get the current
		// value of
		// the stock * the count. Then we update our cash variable.
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
