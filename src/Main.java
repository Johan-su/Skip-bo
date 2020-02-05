import java.util.ArrayList;
import java.util.Collections;

import javax.swing.JOptionPane;

public class Main {
	
	static ArrayList<Card> deck = new ArrayList<Card>();
	static ArrayList<ArrayList<Card>> mainPiles = new ArrayList<ArrayList<Card>>();
	
	static ArrayList<Card> fromDeck, toDeck; //fromDeck is what deck is going to be taken from, toDeck is what deck the card being taken from fromDeck is going to
	
	static int temppos = 0; // if position is optional (from hand) it is chosen by temppos
	static int turns = 0;
	
	static Player[] plist;
	static int currPlayer;
	
	static int stockPile;
	static boolean roundEnd, debug; 
	
	public static void main(String[] args) {
		gameInit();
		
		while (true) { // game loop
			if(deck.size() <= 5 ) createDeck(deck); // create deck if empty or less than a hand size
			
			plist[currPlayer].drawCards(deck); // draws cards
			
			
				while (!roundEnd) {
					for(int i = 0; i < mainPiles.size(); i++) {
						if(mainPiles.get(i).size() == 12) mainPiles.get(i).clear();
					}
					for(int i = 0; i < plist.length; i++) {
						if(plist[i].pPiles.get(0).size() == 0) {
							javax.swing.JOptionPane.showMessageDialog(null, "Player "+i+" Wins");
							System.exit(0);
						}
					}
					doTurn();
				}
			roundEnd=false;
			endTurn();
			turns++;
			
			
			
		}
	}
	static void doTurn() {// Current Player to do its turn
		System.out.println("player: "+(currPlayer+1));
		temppos = 0;
		
		if(plist[currPlayer].hand.size() == 0) { //current player draws cards if their hand is empty during their turn
			plist[currPlayer].drawCards(deck);
			System.out.println("Player "+currPlayer+" got a empty hand"); 
		}
		if(plist[currPlayer].ai == false) {
			playerChoice();
			
		} else randomAi();

		plist[0].playCard(fromDeck, temppos, toDeck);
		
		if(isToMainPile() && toDeck.get(toDeck.size()-1).id == 0) toDeck.get(toDeck.size()-1).id = toDeck.size(); // if Skip-bo card (ID 0) change ID to 1 greater than the top in the deck
		
		if(plist[currPlayer].ai) printBoard();
		
		if(toDeck == plist[currPlayer].pPiles.get(1) || toDeck == plist[currPlayer].pPiles.get(2) || toDeck == plist[currPlayer].pPiles.get(3) || toDeck == plist[currPlayer].pPiles.get(4)) roundEnd = true;;
	}
	static void createPilesFromDeck(ArrayList<Card> deck) { // creates player stock piles from main deck
		for(int i=0; i<plist.length; i++) {
			for(int l = 0; l < stockPile; l++) {
				plist[i].pPiles.get(0).add(deck.get(0));
				deck.remove(0);
			}
		}
	}
	static void endTurn() { // shifts Current player to next player
		System.out.println("endturn");
		currPlayer = (currPlayer+1) % plist.length;
	}
	static boolean checkMove() { //checks if chosen move is allowed
		if(fromDeck == null || toDeck == null) return false;
		if(fromDeck.size() == 0) {
			return false;
		}
		if(isPlayerPileOnBoard()) {
			if(!isToMainPile()) {
				return false;
			}
		   } else if(fromDeck == plist[currPlayer].hand) {
			   	if(toDeck == plist[currPlayer].pPiles.get(0)) {
					return false;
					
					}
		   }
		if(isToMainPile()) {
			if(fromDeck.get(temppos).id != 0) {
				if(toDeck.size() > 0 && fromDeck.get(temppos).id - 1 != toDeck.get(toDeck.size()-1).id ) {
					return false;
				}					
				 if(toDeck.size() == 0) {
					if(fromDeck.get(temppos).id != 0 && fromDeck.get(temppos).id != 1) {
						return false;
						}
					}
				}
			}
		return true;
	}
	static boolean isPlayerPileOnBoard() { // returns true if the deck, the player is taking from is a playerpile
		return  fromDeck == plist[currPlayer].pPiles.get(0) || 
				fromDeck == plist[currPlayer].pPiles.get(1) || 
				fromDeck == plist[currPlayer].pPiles.get(2) || 
				fromDeck == plist[currPlayer].pPiles.get(3) ||
				fromDeck == plist[currPlayer].pPiles.get(4);
	}
	static boolean isToMainPile() { // returns true if the deck, the player is playing to is a MainPile
		return toDeck == mainPiles.get(0) || toDeck == Main.mainPiles.get(1) || toDeck == Main.mainPiles.get(2) || toDeck == Main.mainPiles.get(3);
	}
	static String deckToString(ArrayList<Card> deck, boolean pos) { //convert card list to id and position (if boolean is true) to string
		StringBuilder stringBuilder = new StringBuilder();
		if(pos) {
			for(int i = 0; i<deck.size(); i++) {
				stringBuilder.append(("pos "+i+" "+deck.get(i).id)+"\n");
			}
		} else {
			for(int i = 0; i<deck.size(); i++) {
				stringBuilder.append((deck.get(i).id)+"\n");
			}
		}
		return stringBuilder.toString();
	}
	static void randomAi() { //  generates random choices as a "AI"
		boolean end = false;
		int from, handPos, to;
		while(!end) {
			StringBuilder info = new StringBuilder("ai ");
			
			from = (int) (Math.random()*6);
			handPos = (int) (Math.random()*plist[currPlayer].hand.size());
			to = (int) (Math.random()*8);
			
			if(from == 5) {
				
				fromDeck = plist[currPlayer].hand;
				temppos = handPos;
				
			} else {
				
				fromDeck = plist[currPlayer].pPiles.get(from);
				temppos = fromDeck.size()-1;
				
			}
			if(to < 4) { // if tempt number is less than 4 pick a mainPile else a pPile
				toDeck = mainPiles.get(to);
				
			} else toDeck = plist[currPlayer].pPiles.get(to-3);
			
			if(checkMove()) {
				
				end = true;
				info.append("legal");
				
			} else {
				info.append("illegal");
				
			}
			if(debug) {
				info.append(" move from: "+ from +" handPos: "+ handPos +" to: "+ to);
				System.out.println(info.toString());
			}
		}
		
	}
	static void printBoard() { // shows string with current board state in the game
		StringBuilder info = new StringBuilder();
		info.append("Turn: "+turns+" Board: ");
		info.append(" Deck: "+deck.size());
		
		for(int i=0; i< mainPiles.size(); i++) {
			info.append("\n"+"mPile"+(i+1)+": ");
			if(mainPiles.get(i).size() > 0) {
				info.append(""+(mainPiles.get(i).get(mainPiles.get(i).size()-1)).id);
			}
		}
		info.append("\n");
		if(!(plist[currPlayer].ai)) { // only print non-AI hand
			info.append("Player "+(currPlayer+1)+" Hand:\n");
			info.append(deckToString(plist[currPlayer].hand, true)); // print hand
		}		
		for(int i=0; i< plist.length; i++) { // prints current player
			info.append("  Player "+(i+1)+"\n");
			
			for(int l = 0; l < plist[i].pPiles.size(); l++) {
				info.append("pPile"+l+": ");
				
				if(plist[i].pPiles.get(l).size() > 0) info.append(""+plist[i].pPiles.get(l).get(plist[i].pPiles.get(l).size()-1).id);
				
				info.append("\n");
			}
		}
		javax.swing.JOptionPane.showMessageDialog(null, info.toString());
		
	}
	static void createDeck(ArrayList<Card> deck) { // creates a shuffled Skip-Bo deck
		for(int i = 0; i < 6; i++) {
			deck.add(new Card(0));
		}
		for(int i = 0; i < 13; i++) {
			for(int l = 0; l < 12; l++) {
				deck.add(new Card(i));
			}
		}
		Collections.shuffle(deck);; //randomize deck
	}
	static void playerChoice() { // starts a decision system for players to choose cards and deck.
			String choice = javax.swing.JOptionPane.showInputDialog("what do you want to do? \n1. play card \n2. show all known cards");

			switch(choice) {
			
			case "1": // play card
				String c1 = javax.swing.JOptionPane.showInputDialog("from pile? c to cancel \n pPile0-pPile4, hand0-hand4  type the pile (h/p) type and number");
				
				if(c1.isBlank() || !(c1.charAt(0) == "h".charAt(0) || c1.charAt(0) == "p".charAt(0))) {
					javax.swing.JOptionPane.showMessageDialog(null, "Starting over");
					playerChoice();
				}
				int numberChoice = Character.getNumericValue(c1.charAt(c1.length()-1)); // last number in string determining position/pile
				
				if(c1.charAt(0) == "h".charAt(0)) { // if hand
					
					fromDeck = plist[0].hand;
					temppos = numberChoice; // treats number after h if hand as position in hand
					
				} else { 
					int pilenumber = numberChoice; // treats number after p as pile number
					if(plist[0].pPiles.get(pilenumber).size() != 0) {
						
						fromDeck = plist[0].pPiles.get(pilenumber);
						temppos = fromDeck.size()-1; // if not hand it takes from top of the deck.
						
					} else {
						javax.swing.JOptionPane.showMessageDialog(null, "can't take from empty pile");
						playerChoice();
					}
				}
				String c3 = javax.swing.JOptionPane.showInputDialog("to pile? c to cancel \n mPile1-mPile4 pPile1-pPile4  type the pile (m/p) type and number");
				
				if(!(c3.charAt(0) == "m".charAt(0) || c3.charAt(0) == "p".charAt(0))) playerChoice();
				
				int pileNumber = Character.getNumericValue(c3.charAt(c3.length()-1));
				
				if(c3.charAt(0) == "p".charAt(0)) { // if pPiles (to differentiate between mainPiles and own pPiles)
					toDeck = plist[0].pPiles.get(pileNumber);
						
				} else toDeck = mainPiles.get(pileNumber-1); // -1 for mainPiles for better formating and to be able to start at 1 instead of 0
				
				if(!(checkMove())) {
					javax.swing.JOptionPane.showMessageDialog(null, "ILLEGAL MOVE");
					playerChoice();
				}
				break;
			case "2": // print board
				printBoard();
				playerChoice();
				break;
			
			default:
				playerChoice();
			}

	}
	static void gameInit() { // creates first deck, stockpiles, players and ai
		int max, bots;
		String p = javax.swing.JOptionPane.showInputDialog("TYPE \"debug\" FOR DEBUG");
		if(p.equals("debug")) debug = true;
		bots = stringChoiceMinMax("How many ai? \n min: 1 \n max: 14", 1, 14);
		
		max = (int)((105/(1+bots))-5); // formula for max amount of cards in stockpile with 1+bots because there is always one player
		stockPile = stringChoiceMinMax("how many cards in stock pile max: "+ max +" cards", 1, max);
		
		plist = new Player[1+bots];
		
		plist[0] = new Player(false);
		for(int i = 0; i < bots; i++) plist[i+1] = new Player(true);
		for(int i = 0; i < 4; i++) mainPiles.add(new ArrayList<Card>());   // adds piles to mainPiles arraylist
		createDeck(deck);
		createPilesFromDeck(deck);
		
		currPlayer = (int) (Math.random()*(plist.length)); // randomize starting "player/ai"
	}
	static int inputStringToInt(String s) {
		int i = 0;
		try {
			i = Integer.parseInt(javax.swing.JOptionPane.showInputDialog(s));
		} catch(Exception e) {
			JOptionPane.showMessageDialog(null, "Unexpectedformat, Starting over");
			inputStringToInt(s);
		}
		return i;
	}
	static int stringChoiceMinMax(String s, int min, int max) {
		if(max < min) throw new NumberFormatException("min greater than max");
		int i = inputStringToInt(s);
		if(!(i >= max || i <= min)) stringChoiceMinMax(s, min, max);
		return i;
		
	}
}