package fr.ubx.poo.model.decor;

public class Door extends Decor {
	private boolean open;
	private boolean isNext;

	public Door(boolean open, boolean isNext){
		super();
		this.open = open;
		this.isNext = isNext;
	}
	
    @Override
    public String toString() {
        return "Door";
    }

    public boolean isOpen() {
    	return open;
    }

    public void openDoor(){
	    open = true;
    }

    public boolean isNext(){ return isNext; } // door to go to next ? or previous level ?



    @Override
    public boolean isDoor() {
        return true;
    }
}
