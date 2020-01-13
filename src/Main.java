import java.util.ArrayList;
import java.util.Collections;

public class Main {
	static ArrayList<Card> deck = new ArrayList<Card>();
	static ArrayList<Card> trashdeck = new ArrayList<Card>();
	static ArrayList<ArrayList<Card>> mainPiles = new ArrayList<ArrayList<Card>>();
	//static ArrayList<Card> tmpdeck = new ArrayList<Card>();
	static ArrayList<String> info = new ArrayList<String>();
	public static int round = 0;
	static Player[] plist;
	static int currPlayer;
	static int stockPile,max;
	public static boolean roundEnd;
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
		for(int i = 0; i < 6; i++) {
			deck.add(new Card(0));
		}
		for(int i = 0; i < 13; i++) {
			for(int l = 0; l < 12; l++) {
				deck.add(new Card(i));
			}
		}
		shuffleDeck(deck); //randomize deck
			plist[0] = new Player(false);
		for(int i = 0; i < bots; i++) {
			plist[i+1] = new Player(true);
		}
		createPilesFromDeck(deck);
		while (true) {
			plist[currPlayer].drawCards(deck); //current player draws cards on their turn
			if(plist[currPlayer].ai) { // ai control
				
			} else {
				//currPlayer = (int) (Math.random()*(plist.length)); //randomize starting "player/ai"
				currPlayer = 0;
				while (!roundEnd) {
					doTurn();
				}
			}
			for(int i=0; i<plist.length; i++) {
				if(plist[i].pPiles.get(0).size()==0) {
					javax.swing.JOptionPane.showMessageDialog(null, "Player "+i+" Wins");
					System.exit(0);
				}
			}
			roundEnd=false;
			round++;
		}
	}
	public static void shuffleDeck(ArrayList<Card> deck) {
		Collections.shuffle(deck);
	}
	static void doTurn() {
		String c2="";
		int choice = Integer.parseInt(javax.swing.JOptionPane.showInputDialog("what do you want to do? 1. play card 2. show all known cards"));
		a1:
		if(choice==1) { // play card
			ArrayList<Card> tempf = null;
			ArrayList<Card> tempt = null;
			int temppos = 0;
			String c1 =javax.swing.JOptionPane.showInputDialog("from hand/pile? c to cancel \n pile0-pile4 type the pile Number");
			if(c1.equals("c")) {
				break a1;
			} else if(c1.equals("hand")) {
				tempf=plist[0].hand;
				c2 =javax.swing.JOptionPane.showInputDialog("position? 0-4, c to cancel ");
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
			if(checkMove(tempf, temppos, tempt) == true) {
				plist[0].playCard(tempf, temppos, tempt);
				if(tempt == mainPiles.get(0) || tempt == mainPiles.get(1) || tempt == mainPiles.get(2) || tempt == mainPiles.get(3)) {
					if(tempt.get(tempt.size()-1).id == 0) {
						tempt.get(tempt.size()-1).id = tempt.size();
					}
					for(int i = 0; i < mainPiles.size(); i++) {
						if(mainPiles.get(i).size() > 0 && mainPiles.get(i).get(mainPiles.get(i).size()-1).id == 12) {
							addToTrash(mainPiles.get(i));
							mainPiles.get(i).clear();
						}
					}
				}
			} else {
				javax.swing.JOptionPane.showMessageDialog(null, "ILLEGAL MOVE");
				doTurn();
			}
		} else if(choice==2) {
			info.clear();
			info.add("Board: ");
			for(int i=0; i< mainPiles.size(); i++) {
				info.add("\n"+i+" ");
				if(mainPiles.get(i).size() > 0) {
					info.add(""+(mainPiles.get(i).get(mainPiles.get(i).size()-1)).id);
				} else {
					info.add("");
				}
			}
			info.add("\n"+"Player "+(currPlayer+1)+" Hand:\n");
			info.addAll(printDeck(plist[currPlayer].hand, true));
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
	}
	public static void createPilesFromDeck(ArrayList<Card> deck) { // creates player stock piles from main deck
		for(int i=0; i<plist.length; i++) {
			for(int l = 0; l < stockPile; l++) {
				plist[i].pPiles.get(0).add(deck.get(0));
				deck.remove(0);
			}
		}
	}
	static void endTurn() {
		roundEnd = true;
		System.out.println("endturn");
	}
	public static boolean checkMove(ArrayList<Card> tempf, int temppos, ArrayList<Card> tempt) {
		if(isPlayerPileOnBoard(tempf)) {
			if(!isToMainPile(tempt)) {
				return false;
			}
			return true;
		   } else {
				if(tempf == plist[currPlayer].hand) {
					if(tempt == plist[currPlayer].pPiles.get(0)) {
						return false;
					
					} else if(tempt == plist[currPlayer].pPiles.get(1) || tempt == plist[currPlayer].pPiles.get(2) || tempt == plist[currPlayer].pPiles.get(3) || tempt == plist[currPlayer].pPiles.get(4)) {
						endTurn();
						
						} else if(isToMainPile(tempt)) {
							if(tempf.get(temppos).id != 0) {
								if(tempt.size() > 0 && tempf.get(temppos).id - 1 != tempt.get(tempt.size()-1).id ) {
									return false;
									
								} else if(tempt.size() == 0) {
									if(tempf.get(temppos).id != 0 && tempf.get(temppos).id != 1) {
										return false;
									}
								}
						}
					}
				return true;
			}
			return false;
		}
	}
	static boolean isPlayerPileOnBoard(ArrayList<Card> tempf) {
		return tempf == plist[currPlayer].pPiles.get(0) || 
				tempf == plist[currPlayer].pPiles.get(1) || 
				tempf == plist[currPlayer].pPiles.get(2) || 
				tempf == plist[currPlayer].pPiles.get(3);
		
	}
	static boolean isToMainPile(ArrayList<Card> tempt) {
		return tempt == mainPiles.get(0) || tempt == Main.mainPiles.get(1) || tempt == Main.mainPiles.get(2) || tempt == Main.mainPiles.get(3);
	}
	static ArrayList<String> printDeck(ArrayList<Card> deck, boolean pos) { //convert card list to id and position to string list
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
	public static void addToTrash(ArrayList<Card> deck) {
		trashdeck.addAll(deck);
	}
}