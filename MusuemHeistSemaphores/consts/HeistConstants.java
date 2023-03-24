package consts;

public final class HeistConstants {

	private HeistConstants()
	{
	}


	public static final int NUM_THIEVES = 6;
	public static final int NUM_ROOMS = 5;
	public static final int PARTY_SIZE = 3;
	public static final int MAX_NUM_PARTIES = Math.floorDiv(NUM_THIEVES, PARTY_SIZE);

	public static final int MAX_CRAWLING_DISTANCE = 3;

	public static final int MIN_THIEF_MD = 2;
	public static final int MAX_THIEF_MD = 5;

	public static final int MIN_DISTANCE_OUTSIDE = 15;
	public static final int MAX_DISTANCE_OUTSIDE = 30;

	public static final int MIN_NUM_PAINTINGS = 8;
	public static final int MAX_NUM_PAINTINGS = 16;

	public static String printConsts() {
		return new StringBuilder()
			.append(String.format("\tNUM_THIEVES: %d\n", NUM_THIEVES))
			.append(String.format("\tNUM_ROOMS: %d\n", NUM_ROOMS))
			.append(String.format("\tPARTY_SIZE: %d\n", PARTY_SIZE))
			.append(String.format("\tMAX_CRAWLING_DISTANCE: %d\n", MAX_CRAWLING_DISTANCE))
			.append(String.format("\tMIN_THIEF_MD: %d\n", MIN_THIEF_MD))
			.append(String.format("\tMAX_THIEF_MD: %d\n", MAX_THIEF_MD))
			.append(String.format("\tMIN_DISTANCE_OUTSIDE: %d\n", MIN_DISTANCE_OUTSIDE))
			.append(String.format("\tMAX_DISTANCE_OUTSIDE: %d\n", MAX_DISTANCE_OUTSIDE))
			.append(String.format("\tMIN_NUM_PAINTINGS: %d\n", MIN_NUM_PAINTINGS))
			.append(String.format("\tMAX_NUM_PAINTINGS: %d\n", MAX_NUM_PAINTINGS))
			.toString();
	}

}
