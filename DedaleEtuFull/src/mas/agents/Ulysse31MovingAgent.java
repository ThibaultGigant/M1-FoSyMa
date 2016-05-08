package src.mas.agents;

import mas.abstractAgent;

public class Ulysse31MovingAgent extends abstractAgent {
	private static final long serialVersionUID = -5686331366676803589L;
	protected void setup(){//Automatically called at agent’s creation
		super.setup();
	}
	protected void beforeMove(){//Automatically called before doMove()
		super.beforeMove();
		System.out.println("I migrate");
	}
	protected void afterMove(){//Automatically called after doMove()
		super.afterMove();
		System.out.println("I migrated");
	}
}
