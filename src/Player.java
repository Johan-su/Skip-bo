import java.util.ArrayList;

public class Player {
	ArrayList<ArrayList<Card>> pPiles = new ArrayList<ArrayList<Card>>();
	ArrayList<Card> hand = new ArrayList<Card>();
	boolean ai;
	
	public Player(boolean ai) {
		this.ai=ai;
		for(int i = 0; i < 5; i++) {
			pPiles.add(new ArrayList<Card>());
		}
	}
	public void playCard(ArrayList<Card> f, int pos, ArrayList<Card> t) {
		t.add(f.get(pos));
		f.remove(pos);
	}
	public void drawCards(ArrayList<Card> f) {
		System.out.println("player "+(Main.currPlayer+1)+" draws Cards");
		int tempsize = hand.size();
		for(int i = 0; i < (5 - tempsize); i++) {
			hand.add(f.get(f.size() - 1));
			f.remove(f.size() - 1);
		}
	}

}
