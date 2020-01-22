import java.util.ArrayList;
import java.util.Collections;

public class Main {
	static ArrayList<Card> deck = new ArrayList<Card>();
	
	static ArrayList<ArrayList<Card>> mainPiles = new ArrayList<ArrayList<Card>>();
	static ArrayList<String> info = new ArrayList<String>();
	
	static ArrayList<Card> tempf = null;
	static ArrayList<Card> tempt = null;
	
	static int temppos = 0;
	static int round = 0;
	
	static Player[] plist;
	static int currPlayer;
	
	static int stockPile,max;
	static boolean roundEnd;
	
	public static void main(String[] args) {
		for(int i = 0; i < 4; i++) {
			mainPiles.add(new ArrayList<Card>());   
		}
		
		int bots = Integer.parseInt(javax.swing.JOptionPane.showInputDialog("how many AI"));
		if((bots) < 1 || (bots) > 14) {
			main(args);
		}
		max = (int)((105/(1+bots))-5); // formula for max amount of cards in stockpile with 1+bots because there is always one player
		stockPile = Integer.parseInt(javax.swing.JOptionPane.showInputDialog("how many cards in stock pile max: "+max+" cards"));
		if(stockPile > max || max <= 0) { 
			main(args);
		}
		plist = new Player[1+bots];
			plist[0] = new Player(true);
			//plist[0] = new Player(false);
		for(int i = 0; i < bots; i++) {
			plist[i+1] = new Player(true);
		}
		createDeck(deck);
		createPilesFromDeck(deck);
		currPlayer = (int) (Math.random()*(plist.length)); //randomize starting "player/ai"
		
		while (true) {
			System.out.println("decksize "+deck.size());
			System.out.println("round "+round);
			if(deck.size() <= 5 ) { // create deck if empty
				createDeck(deck);
			}
			plist[currPlayer].drawCards(deck); // draws cards
			//currPlayer = 0;
				while (!roundEnd) {
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
			round++;
		}
	}
	static void shuffleDeck(ArrayList<Card> deck) {
		Collections.shuffle(deck);
	}
	static void doTurn() {
		System.out.println("player: "+(currPlayer+1));
		tempf = null;
		tempt = null;
		temppos = 0;
		if(plist[currPlayer].hand.size() == 0) { //current player draws cards if their hand is empty during their turn
			plist[currPlayer].drawCards(deck);
			System.out.println("Player "+currPlayer+" got a empty hand"); 
		}
		if(plist[currPlayer].ai == false) {
		String c2="";
		int choice = Integer.parseInt(javax.swing.JOptionPane.showInputDialog("what do you want to do? 1. play card 2. show all known cards"));
		a1:
		if(choice==1) { // play card
			String c1 = javax.swing.JOptionPane.showInputDialog("from hand/pile? c to cancel \n pile0-pile4 type the pile Number");
			if(c1.equals("c")) {
				break a1;
			} else if(c1.equals("hand")) {
				tempf = plist[0].hand;
				c2 = javax.swing.JOptionPane.showInputDialog("position? 0-4, c to cancel ");
				if(c2.equals("c")) {
					break a1;
				} else {
					temppos = Integer.valueOf(c2);
				}
			} else if(Integer.valueOf(c1) != null) {
				if(plist[0].pPiles.get(Integer.valueOf(c1)).size() != 0) {
					tempf=plist[0].pPiles.get(Integer.valueOf(c1));
				} else {
					javax.swing.JOptionPane.showMessageDialog(null, "cant take from empty pile");
					break a1;
				}
				
			}
			if(!(c1.equals("hand"))) {
				temppos= tempf.size()-1;
			}
			if(c1.equals("c")) {
				break a1;
			}
			String c3 =javax.swing.JOptionPane.showInputDialog("to pile? c to cancel \n mainpile0-mainpile3 pPile4-pPile7  type the pile number");
			if(c3.equals("c")) {
				break a1;
			} else if(Integer.valueOf(c3) != null) {
				if(Integer.valueOf(c3) > 3) {
					tempt=plist[0].pPiles.get(Integer.valueOf(c3)-3);
				} else {
					tempt=mainPiles.get(Integer.valueOf(c3));
				}
			}
			if(checkMove(tempf, temppos, tempt)) {
				plist[0].playCard(tempf, temppos, tempt);
				if(isToMainPile(tempt)) {
					if(tempt.get(tempt.size()-1).id == 0) {
						tempt.get(tempt.size()-1).id = tempt.size();
					}
				}
			} else {
				javax.swing.JOptionPane.showMessageDialog(null, "ILLEGAL MOVE");
				doTurn();
			}
		} else if(choice==2) printBoard();
		
		} else {
			randomAi();
			plist[currPlayer].playCard(tempf, temppos, tempt);
			if(isToMainPile(tempt)) {
				if(tempt.size() > 0 && tempt.get(tempt.size()-1).id == 0) {
					tempt.get(tempt.size()-1).id = tempt.size();
				}
			}
			//printBoard();
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
	static void endTurn() {
		//roundEnd = true;
		System.out.println("endturn");
		currPlayer = (currPlayer+1) % plist.length;
	}
	static boolean checkMove(ArrayList<Card> tempf, int temppos, ArrayList<Card> tempt) { //checks if chosen move is allowed
		if(tempf == null || tempt == null) return false;
		if(tempf.size() == 0) {
			return false;
		}
		if(isPlayerPileOnBoard(tempf)) {
			if(!isToMainPile(tempt)) {
				return false;
			}
		   } else if(tempf == plist[currPlayer].hand) {
			   	if(tempt == plist[currPlayer].pPiles.get(0)) {
					return false;
					
					}
		   }
		if(isToMainPile(tempt)) {
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
	static boolean isPlayerPileOnBoard(ArrayList<Card> tempf) {
		return  tempf == plist[currPlayer].pPiles.get(0) || 
				tempf == plist[currPlayer].pPiles.get(1) || 
				tempf == plist[currPlayer].pPiles.get(2) || 
				tempf == plist[currPlayer].pPiles.get(3) ||
				tempf == plist[currPlayer].pPiles.get(4);
		
	}
	static boolean isToMainPile(ArrayList<Card> tempt) {
		return tempt == mainPiles.get(0) || tempt == Main.mainPiles.get(1) || tempt == Main.mainPiles.get(2) || tempt == Main.mainPiles.get(3);
	}
	static ArrayList<String> deckToString(ArrayList<Card> deck, boolean pos) { //convert card list to id and position to string list
		ArrayList<String> p = new ArrayList<String>();
		if(pos) {
			for(int i=0; i<deck.size(); i++) {
				//System.out.println("pos "+i+deck.get(i).id);
				p.add(("pos "+i+" "+deck.get(i).id)+"\n");
			}
		} else {
			for(int i=0; i<deck.size(); i++) {
				//System.out.println(deck.get(i).id);
				p.add(String.valueOf((deck.get(i).id)+"\n"));
			}
		}
		return p;
	}
	static void randomAi() {
		boolean end = false;
		while(!end) {
		int fromr = (int) (Math.random()*6);
		int fromHandPos = (int) (Math.random()*plist[currPlayer].hand.size());
		int tor = (int) (Math.random()*8);
		if(fromr == 5) {
			tempf = plist[currPlayer].hand;
			temppos = fromHandPos;
		} else {
			tempf = plist[currPlayer].pPiles.get(fromr);
			temppos = tempf.size()-1;
		}
		if(tor < 4) {
			tempt = mainPiles.get(tor);
		} else {
			tempt = plist[currPlayer].pPiles.get(tor-3);
		}
		if(checkMove(tempf, temppos, tempt)) {
			end = true;
			System.out.println("ai   legal move "+"fromr: "+fromr+" fromHandPos: "+fromHandPos+" tor: "+tor);
		} else {
			System.out.println("ai illegal move "+"fromr: "+fromr+" fromHandPos: "+fromHandPos+" tor: "+tor);
			if(tempt.size() > 0 && tempf.size() > 0) {
				System.out.println("from "+ tempf.get(temppos)+" to "+ tempt.get(tempt.size()-1));
			} else if(tempf.size() > 0){
				System.out.println("from "+ tempf.get(temppos)+" to empty");
			}
			
		}
	}
		
	}
	static void printBoard() {
		info.clear();
		info.add("round: "+round);
		info.add("Board: ");
		for(int i=0; i< mainPiles.size(); i++) {
			info.add("\n"+i+" ");
			if(mainPiles.get(i).size() > 0) {
				info.add(""+(mainPiles.get(i).get(mainPiles.get(i).size()-1)).id);
			}/* else {
				info.add("");
			}*/
		}
		info.add("\n"+"Player "+(currPlayer+1)+" Hand:\n");
		info.addAll(deckToString(plist[currPlayer].hand, true));
		for(int i=0; i< plist.length; i++) {
			info.add("  Player "+(i+1)+"\n");
			for(int l = 0; l < plist[i].pPiles.size(); l++) {
				//info.add("pile" + l + " ");
				//info.addAll(printDeck(plist[i].pPiles.get(l), true));;
				if(plist[i].pPiles.get(l).size() > 0) {
					info.add("pile"+l+": card "+plist[i].pPiles.get(l).get(plist[i].pPiles.get(l).size()-1).id+"\n");
				} else {
					info.add("pile"+l+": \n");
				}
				info.add("\n");
			}
		}
		javax.swing.JOptionPane.showMessageDialog(null, info.toString()); // shows string with current board state in the game
		
	}
	static void createDeck(ArrayList<Card> deck) {
		for(int i = 0; i < 6; i++) {
			deck.add(new Card(0));
		}
		for(int i = 0; i < 13; i++) {
			for(int l = 0; l < 12; l++) {
				deck.add(new Card(i));
			}
		}
		shuffleDeck(deck); //randomize deck
	}
}