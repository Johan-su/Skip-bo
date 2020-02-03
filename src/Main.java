import java.util.ArrayList;
import java.util.Collections;

public class Main {
	
	static ArrayList<Card> deck = new ArrayList<Card>();
	static ArrayList<ArrayList<Card>> mainPiles = new ArrayList<ArrayList<Card>>();
	
	static ArrayList<Card> tempf, tempt; //tempf is what deck is going to be taken from, tempt is what deck the card being taken from tempf is going to
	
	static int temppos = 0; // if position is optional (from hand) it is chosen by temppos
	static int turns = 0;
	
	static Player[] plist;
	static int currPlayer;
	
	static int stockPile;
	static boolean roundEnd, debug;
	
	public static void main(String[] args) {
		gameInit();
		
		
		
		while (true) { // game loop
			//System.out.println("decksize "+deck.size());
			//System.out.println("turn "+turns);
			//if(turns == 100000) printBoard();
			if(deck.size() <= 5 ) { // create deck if empty
				createDeck(deck);
			}
			plist[currPlayer].drawCards(deck); // draws cards
			//currPlayer = 0;
			
			
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
		} else {
			randomAi();
			plist[currPlayer].playCard(tempf, temppos, tempt);
			if(isToMainPile()) {
				if(tempt.size() > 0 && tempt.get(tempt.size()-1).id == 0) {
					tempt.get(tempt.size()-1).id = tempt.size();
				}
			}
			printBoard();
		}
		if(tempt == plist[currPlayer].pPiles.get(1) || tempt == plist[currPlayer].pPiles.get(2) || tempt == plist[currPlayer].pPiles.get(3) || tempt == plist[currPlayer].pPiles.get(4)) roundEnd = true;;
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
		if(tempf == null || tempt == null) return false;
		if(tempf.size() == 0) {
			return false;
		}
		if(isPlayerPileOnBoard()) {
			if(!isToMainPile()) {
				return false;
			}
		   } else if(tempf == plist[currPlayer].hand) {
			   	if(tempt == plist[currPlayer].pPiles.get(0)) {
					return false;
					
					}
		   }
		if(isToMainPile()) {
			if(tempf.get(temppos).id != 0) {
				if(tempt.size() > 0 && tempf.get(temppos).id - 1 != tempt.get(tempt.size()-1).id ) {
					return false;
				}					
				 if(tempt.size() == 0) {
					if(tempf.get(temppos).id != 0 && tempf.get(temppos).id != 1) {
						return false;
						}
					}
				}
			}
		return true;
	}
	static boolean isPlayerPileOnBoard() { // returns true if the deck, the player is taking from is a playerpile
		return  tempf == plist[currPlayer].pPiles.get(0) || 
				tempf == plist[currPlayer].pPiles.get(1) || 
				tempf == plist[currPlayer].pPiles.get(2) || 
				tempf == plist[currPlayer].pPiles.get(3) ||
				tempf == plist[currPlayer].pPiles.get(4);
		
	}
	static boolean isToMainPile() { // returns true if the deck, the player is playing to is a MainPile
		return tempt == mainPiles.get(0) || tempt == Main.mainPiles.get(1) || tempt == Main.mainPiles.get(2) || tempt == Main.mainPiles.get(3);
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
				
				tempf = plist[currPlayer].hand;
				temppos = handPos;
				
			} else {
				
				tempf = plist[currPlayer].pPiles.get(from);
				temppos = tempf.size()-1;
				
			}
			if(to < 4) { // if tempt number is less than 4 pick a mainPile else a pPile
				tempt = mainPiles.get(to);
				
			} else tempt = plist[currPlayer].pPiles.get(to-3);
			
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
		int choice = Integer.parseInt(javax.swing.JOptionPane.showInputDialog("what do you want to do? 1. play card 2. show all known cards"));
		a1:
		if(choice == 1) { // play card
			String c1 = javax.swing.JOptionPane.showInputDialog("from pile? c to cancel \n pPile0-pPile4, hand0-hand4  type the pile (h/p) type and number");
			if(!(c1.charAt(0) == "h".charAt(0) || c1.charAt(0) == "p".charAt(0))) break a1;
			
			if(c1.charAt(0) == "h".charAt(0)) {
				
				tempf = plist[0].hand;
				temppos = Character.getNumericValue(c1.charAt(c1.length()-1));
				
			} else {
				int pilenumber = Character.getNumericValue(c1.charAt(c1.length()-1));
				if(plist[0].pPiles.get(pilenumber).size() != 0) {
					
					tempf = plist[0].pPiles.get(pilenumber);
					temppos = tempf.size()-1; // if not hand it takes from top of the deck.
					
				} else {
					javax.swing.JOptionPane.showMessageDialog(null, "cant take from empty pile");
					break a1;
				}
			}
			String c3 = javax.swing.JOptionPane.showInputDialog("to pile? c to cancel \n mainpile1-mainpile4 pPile1-pPile4  type the pile (m/p) type and number");
			if(!(c3.charAt(0) == "m".charAt(0) || c3.charAt(0) == "p".charAt(0))) break a1;
			
			int pileNumber = Character.getNumericValue(c3.charAt(c3.length()-1));
			
			if(c3.charAt(0) == "p".charAt(0)) { // if pPiles to differentiate between mainPiles and own pPiles
				tempt = plist[0].pPiles.get(pileNumber);
					
			} else tempt = mainPiles.get(pileNumber-1); // -1 for mainPiles for better formating and to be able to start at 1 instead of 0
			
			if(checkMove()) {
				plist[0].playCard(tempf, temppos, tempt);
				if(isToMainPile() && tempt.get(tempt.size()-1).id == 0) tempt.get(tempt.size()-1).id = tempt.size(); // if Skip-bo card (ID 0) change ID to 1 greater than the top in the deck
			} else {
				javax.swing.JOptionPane.showMessageDialog(null, "ILLEGAL MOVE");
				doTurn();
			}
		} else if(choice == 2) printBoard();
		
	}
	static void gameInit() { // creates first deck, stockpiles, players and ai
		int max;
		String p = javax.swing.JOptionPane.showInputDialog("TYPE \"debug\" FOR DEBUG");
		if(p.equals("debug")) debug = true;
		
		for(int i = 0; i < 4; i++) mainPiles.add(new ArrayList<Card>());   // adds piles to mainPiles arraylist
		
		int bots = Integer.parseInt(javax.swing.JOptionPane.showInputDialog("how many AI"));
		if((bots) < 1 || (bots) > 14) gameInit();
		
		max = (int)((105/(1+bots))-5); // formula for max amount of cards in stockpile with 1+bots because there is always one player
		stockPile = Integer.parseInt(javax.swing.JOptionPane.showInputDialog("how many cards in stock pile max: "+max+" cards"));
		
		if(stockPile > max || max <= 0) gameInit();
		plist = new Player[1+bots];
		
		plist[0] = new Player(false);
		for(int i = 0; i < bots; i++) plist[i+1] = new Player(true);
		
		createDeck(deck);
		createPilesFromDeck(deck);
		
		currPlayer = (int) (Math.random()*(plist.length)); // randomize starting "player/ai"
	}
}