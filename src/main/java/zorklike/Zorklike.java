package zorklike;

//i need to add subjective verbs (i.e. things like "door" where itll assume you mean the most recently mentioned door)
//also i need to add and functionality to some of the commands ("grab the key and the axe" will grab the key as it is the first mentioned word but not the axe)
//actually that should be working fine hold on im looking into it

//import statements
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Scanner;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Zorklike {
	//init variables
	static Scanner scan;
	static Dictionary dictionary;
	static enum Type {
		KEY,
		CD,
		WEAPON,
		RANDOM,
	};
	public static List<Item> inventory;
	public static List<Room> rooms;
	//abstract command method
	@FunctionalInterface
	public static interface Command {
		int command(String action, ArrayList<String> objects, ArrayList<String> targets);
	}
	//hashmap for commands
	public static HashMap<String, Command> commandHashMap;
	public static final String redBackground = "\033[41m";
	public static final String resetFormatting = "\033[0m";
	public static final String blueColor = "\u001B[34m";
	public static final String boldBlueColor = "\033[1;34m";
	public static final String boldRedColor = "\033[1;31m";
	public static final String purpleColor = "\u001B[35m";
	public static final String greenColor = "\u001B[32m";
	public static final String greenBackground = "\u001B[42m";
	public static final String yellowColor = "\u001B[33m";
	public static final String yellowBackground = "\u001B[43m";
	public static final String cyanColor = "\u001B[36m";
	public static final String blueBackground = "\u001B[44m";
	public static final String blackColor = "\u001B[30m";
	public static final String brightYellowColor = "\033[0;93m";
	public static final String cyanBackground = "\033[46m";
	public static final String boldWhiteColor = "\033[1;37m";
	public static final String italics = "\033[3m";
	public static final String clear = "\033[H\033[2J";
	// custom functions
	public static boolean containsExactWord(String mainStr, String word) {
		if(mainStr==null||word==null){return false;}
		// \\b is a regex word boundary
		// Pattern.CASE_INSENSITIVE makes it not case sensitive
		Pattern pattern = Pattern.compile("\\b" + Pattern.quote(word) + "\\b",Pattern.CASE_INSENSITIVE);
		// System.out.println("main="+mainStr+"\npattern="+pattern+"\n");
		Matcher matcher = pattern.matcher(mainStr);
		return matcher.find();
	}

	// entry plug
	public static void main(String[] args) {
		//declare init variables
		boolean run = true;
		inventory = new ArrayList<Item>();
		rooms = new ArrayList<Room>();
		commandHashMap = new HashMap<String, Command>();
		scan = new Scanner(System.in);
		//create rooms

		//testroom (requires key and axe from all connections)
		rooms.add(new Room("testroom","test room","its testroom oh yeah",new Connection("front","testroom2",false,"key"),new Connection("right","testroom3",true)));
		//testroom items
		rooms.get(0).addFurniture(new Furniture("table","wooden table", "A wooden table stands in the corner",false,true,new Item(Type.KEY,"key","simple key","A small silver key. It feels cold to the touch.",true,0,0,null),new Item(Type.WEAPON,"axe","fireaxe","A hefty red and yellow axe similar to those that the local fire department uses. How did this end up here?",true,10,100,null)));
		// in front of you, testroom2, to your right, testroom3

		//testroom2
		rooms.add(new Room("testroom2","testing room travel","yuhhhh",new Connection("back","testroom",true,"key","axe")));
		//testroom2 items
		rooms.get(1).addFurniture(new Furniture("metal chest","a metal chest","A locked iron chest sits in the center of the room. You need the color gray to unlock this.",true,false,new Item(Type.RANDOM,"lint","ball of lint","A ball of lint",false,0,0,null)).addRequirements("gray"));

		// behind you, testroom

		//testroom3
		rooms.add(new Room("testroom3","testing room travel","yuh its testroom3", new Connection("left","testroom",true)));
		rooms.get(2).addFurniture(new Furniture("countertop","a marble countertop","the marble glistens",false,true,new Item(Type.CD,"flash drive","small red 16gb flash drive","A small red flash drive, labeled \"Daravi 16GB\". A ring attached at the back of the drive lets you push the port outwards and pull it inwards.",true,0,0,"123.cmd"),new Item(Type.KEY,"gray","the color gray","its literally just gray",true,0,0,null)));

		//current room variable (for travel)
		Room[] curRoom = {rooms.get(0)};

		//init dictionary
		dictionary = new Dictionary();

		//command and populate hashmap
		//inventory
		Command checkInventory = (String action, ArrayList<String> targets, ArrayList<String> objects) -> {
			if (inventory.size()==0) {
				System.out.println("Peeking into your backpack, you find nothing.");
				return 0;
			}
			else {
				System.out.println("You have a total of " + inventory.size() + " items in your backpack.");
				int current = 1;
				for (Item item : inventory) {
					String[] check = item.getDescription().split("");
					if (check[0].toLowerCase().equals("a") || check[0].toLowerCase().equals("e") || check[0].toLowerCase().equals("i") || check[0].toLowerCase().equals("o") || check[0].toLowerCase().equals("u")) {
						System.out.println(current + ": An " + item.getDescription() + ".");
					}
					else {
						System.out.println(current + ": A " + item.getDescription() + ".");
					}
					current++;
				}
				return 0;
			}
		};
		commandHashMap.put("inventory",checkInventory);
		commandHashMap.put("backpack",checkInventory);

		//check items
		Command checkItemList = (String action, ArrayList<String> targets, ArrayList<String> objects) -> {
			for (String itemName : dictionary.getItemNames()) {
				System.out.println(itemName);
			}
			return 0;
		};
		commandHashMap.put("list",checkItemList);

		//look around
		Command lookAround = (String action, ArrayList<String> objects, ArrayList<String> targets) -> {
			curRoom[0].getInfo(null);
			return 0;
		};
		commandHashMap.put("around",lookAround);

		//movement using go or move
		Command moveRooms = (String action, ArrayList<String> objects, ArrayList<String> targets) -> {
			if (targets!=null) {
				if (targets.size() > 1) {
					System.out.println(redBackground + "Hey Sonic, you can only travel to one room per command. Slow down. I will only move you to the first room in that list." + resetFormatting);
				}
				Connection[] connectionList = curRoom[0].getConnections();
				for (Connection connect : connectionList) {
					// multi word target gets here then just doesnt print anything.... why???
					if (containsExactWord(targets.get(0),connect.getName())) {
						if (connect.isOpen()) {
							boolean roomExists = false;
							for (Room room : rooms) {
								if (containsExactWord(targets.get(0),room.getName())) {
									roomExists = true;
									curRoom[0] = room;
									System.out.print("You are now in " + room.getName() + ". " + room.getDescription() + ".\n");
									curRoom[0].getInfo(null);
									return 0;
								}
							}
							if (!roomExists) {
								System.out.println(redBackground + "ERROR: ROOM " + targets.get(0) + " DOES NOT EXIST." + resetFormatting);
							}
						}
						else {
							System.out.println("That door is locked. You need:");
							int i=0;
							for (String req : connect.getRequirements()) {
								i++;
								System.out.println("    " + boldBlueColor + i + ": " + resetFormatting + req);
							}
							return 0;
						}
					}
					else if (targets==null) {
						System.out.println("...Where?");
						return 0;
					}
				}
			}
			System.out.println("Nuh uh");
			return 0;
		};
		commandHashMap.put("go",moveRooms);
		commandHashMap.put("move",moveRooms);
		//movement using directions
		Command moveDirection = (String action, ArrayList<String> objects, ArrayList<String> targets) -> {
			String act = action;
			if (act.equals("foreward") || act.equals("forewards")) {
				act = "front";
			}
			else if (act.equals("backwards") || act.equals("backward")) {
				act = "back";
			}
			for (Connection connect : curRoom[0].getConnections()) {
				if (connect.getSide().equals(act)) {
					if (connect.isOpen()) {
						String name = connect.getName();
						String[] upper = name.split("");
						upper[0] = upper[0].toUpperCase();
						String fixed = String.join("",upper);
						for (Room room : rooms) {
							if (containsExactWord(name,room.getName())) {
								curRoom[0] = room;
								System.out.print("You are now in " + fixed + ". " + room.getDescription() + ".\n");
								curRoom[0].getInfo(null);
								return 0;
							}
						}
					}
					else {
						System.out.println("That door is locked. You need:");
						int i=0;
						for (String req : connect.getRequirements()) {
							i++;
							System.out.println("    " + boldBlueColor + i + ": " + resetFormatting + req);
						}
						return 0;
					}
				}
			}
			System.out.println("You can't walk through a wall...");
			return 0;
		};
		for (String command : Dictionary.directions) {
			commandHashMap.put(command,moveDirection);
		}

		//grabbing items
		Command grabItem = (String action, ArrayList<String> objects, ArrayList<String> targets) -> {
			// currently rewriting
			List<Furniture> furnl = curRoom[0].getFurnL();
			// start
			for (int x=0;x<furnl.size();x++) {
				Furniture curfurn = furnl.get(x);
				List<Item> iteml = curfurn.getItemL();
				if (iteml.size()==0) {
					System.out.println("No more items to pick up here, sorry.");
				}
				List<String> requirements = curfurn.getRequirements();
				if (requirements==null) {
					for (Iterator<Item> i=iteml.iterator();i.hasNext();) {
						Item item = i.next();
						System.out.println(item.getName());
						boolean itemInRoom = false;
						if (objects==null) {
							for (Iterator<String> y=targets.iterator();y.hasNext();) {
								if (containsExactWord(y.next(),item.getName())) {
									inventory.add(item);
									i.remove();
									y.remove();
									System.out.println("You grab the " + item.getName() + " and put it into your backpack.");
									itemInRoom = true;
									break;
								}
							}
							if (!itemInRoom && targets.size()!=0) {
								System.out.println(redBackground + "There is no " + targets.get(0) + " in this room." + resetFormatting);
							}
						}
						else {
							for (Iterator<String> y=objects.iterator();y.hasNext();) {
								if (containsExactWord(y.next(),item.getName())) {
									inventory.add(item);
									i.remove();
									y.remove();
									System.out.println("You grab the " + item.getName() + " and put it into your backpack.");
									itemInRoom = true;
									break;
								}
							}
							if (!itemInRoom && objects.size()!=0) {
								System.out.println(redBackground + "There is no " + objects.get(0) + " in this room." + resetFormatting);
								objects.remove(0);
							}
						}
					}
				}
				else {
					System.out.println("It's locked. No can do, buckaroo.");
				}
			}
			//end
			if (objects==null) {
				for (Iterator<String> x=targets.iterator();x.hasNext();) {
					boolean itemInRoom = false;
					String curTarget = x.next();
					for (int y=0;y<furnl.size();y++) {
						Furniture curfurn = furnl.get(y);
						List<Item> iteml = curfurn.getItemL();
						List<String> requirements = curfurn.getRequirements();
						if (requirements==null) {
							for (Iterator<Item> z=iteml.iterator();z.hasNext();) {
								Item item = z.next();
								if (containsExactWord(curTarget,item.getName())) {
									inventory.add(item);
									z.remove();
									System.out.println("You grab the " + item.getName() + " and put it into your backpack.");
									itemInRoom = true;
									break;
								}
							}
						}
						else {
							System.out.println("It's locked. No can do, buckaroo.");
						}
					}
					if (!itemInRoom) {
						System.out.println(redBackground + "There is no " + curTarget + " in this room." + resetFormatting);
					}
				}
			}
			return 0;
		};
		for (String cmd : Dictionary.obtaining) {
			commandHashMap.put(cmd,grabItem);
		}

		//examine an object/item
		Command examine = (String action, ArrayList<String> objects, ArrayList<String> targets) -> {
			if (!(targets==null && objects==null)) {
				//examining an object in the inventory
				if (targets==null) {
					for (String object : objects) {
						for (Item item : inventory) {
							if (containsExactWord(object,item.getName())) {
								System.out.println(item.getExtendedDescription());
								break;
							}
							else {
								System.out.println("The " + object + " isn't in your inventory, sorry!");
							}
						}
					}
				}
				//examining a room
				else if (targets!=null) {
					boolean checkRooms = dictionary.searchRooms(targets.get(0).toLowerCase());
					boolean roomSuccess = false;
					boolean checkFurniture = dictionary.searchFurniture(targets.get(0).toLowerCase());
					boolean furnSuccess = false;
					//examining rooms
					if (checkRooms) {
						if (targets.size() > 1) {
							System.out.println("You've only got one set of eyes, so you can only peer in one room. I'll only let you look at the first room you listed.");
						}
						for (Room room : rooms) {
							if (containsExactWord(targets.get(0),room.getName())) {
								room.getInfo(curRoom[0].getConnections());
								roomSuccess = true;
							}
						}
					}
					//examining furniture
					else if (checkFurniture) {
						List<Furniture> furnl = new ArrayList<Furniture>();
						for (Room room : rooms) {
							List<Furniture> rfurnl = new ArrayList<Furniture>(room.getFurnL());
							for (Furniture furn : rfurnl) {
								furnl.add(furn);
							}
						}
						for (String target : targets) {
							boolean isInRoom = false;
							for (Furniture furn : furnl) {
								if (containsExactWord(target,furn.getName())) {
									for (Furniture furnr : curRoom[0].getFurnL()) {
										if (containsExactWord(furn.getName(),furnr.getName())) {
											isInRoom = true;
											System.out.println("You examine the " + furn.getName() + ":");
											System.out.println("    " + furn.getExtendedDescription());
											if (!furn.isOpen()) {
												System.out.println("    You need the following to open it:");
												int i=0;
												for (String req : furn.getRequirements()) {
													i++;
													System.out.println("        " + boldBlueColor + i + ": " + resetFormatting + req);
												}
											}
											List<Item> citeml = furn.getItemL();
											if (furn.isOpen()) {
												if (citeml!=null) {
													List<String> tempNameStorage = new ArrayList<String>();
													for (Item it : citeml) {
														String itnm = it.getName();
														List<String> aOrAn = new ArrayList<String>(Arrays.asList(itnm.split("")));
														if (aOrAn.get(0).toLowerCase().equals("a")||aOrAn.get(0).toLowerCase().equals("e")||aOrAn.get(0).toLowerCase().equals("i")||aOrAn.get(0).toLowerCase().equals("o")||aOrAn.get(0).toLowerCase().equals("u")) {
															tempNameStorage.add("an " + itnm);
														}
														else {
															tempNameStorage.add("a " + itnm);
														}
													}
													if (tempNameStorage.size()>1) {
														tempNameStorage.set(tempNameStorage.size()-1,"and " + (tempNameStorage.get(tempNameStorage.size()-1)));
													}
													else if (tempNameStorage.size()>2) {
														if (furn.isContainer()) {
															System.out.println("    Inside, there is " + String.join(", ",tempNameStorage) + ".");
														}
														else {
															System.out.println("    On top, there is " + String.join(", ",tempNameStorage) + ".");
														}
													}
													else {
														if (furn.isContainer()) {
															if (tempNameStorage.size()>0){
																System.out.println("    Inside, there is " + String.join(" ",tempNameStorage) + ".");
															}
															else {
																System.out.println("There is nothing inside.");
															}
														}
														else {
															if (tempNameStorage.size()>0) {
																System.out.println("    On top, there is " + String.join(" ",tempNameStorage) + ".");
															}
															else {
																System.out.println("There is nothing on it.");
															}
														}
													}
												}
											}
											else {
												System.out.println("    You try to peer inside, but it is closed.");
											}
											furnSuccess = true;
										}
									}
								}
							}
							if (!isInRoom)	{
								System.out.println("There is no " + target + " in this room.");
							}
						}
					}
				}
			}
			else {
				System.out.println("No clue what you're trying to examine, sorry.");
			}
			return 0;
		};
		commandHashMap.put("examine",examine);
		commandHashMap.put("look",examine);
		commandHashMap.put("peer",examine);

		//find command
		Command find = (String action, ArrayList<String> objects, ArrayList<String> targets) -> {
			if (objects==null && targets==null) {
				System.out.println("Find... what, exactly?");
			}
			else {
				System.out.println("Not telling!!!! :D");
			}
			return 0;
		};
		commandHashMap.put("find",find);

		//open/unlocking
		// ok so what ya gotta do (because items and rooms can be both objects and items) is this
		/* 
			 if the target is an openable and there is no object, search entire inventory for required items and ask the user if they want to use all the items
			 if object is item and target is openable, use the object to try and open the target
			 if target is openable and item is object, use the target to try and open the object
			 also add logic for already unlocked doors
			 */
		Command open = (String action, ArrayList<String> objects, ArrayList<String> targets) -> {
			//there can only be one unlocker
			//therefore, if there are more than one unlockable, return that you cant do that.
			//if there is at least one unlockable in the total list of targets, return the same error as before
			int itemIsTarget = dictionary.searchItems(targets);
			String openable = "";
			ArrayList<String> unlockers = new ArrayList<>();
			if (itemIsTarget==1) {
				unlockers = targets;
				openable = objects.get(0);
			}
			else if (itemIsTarget==0) {
				unlockers = objects;
				openable = targets.get(0);
			}
			else if (itemIsTarget==2) {
				System.out.println("Please refrain from using openable objects as keys.");
			}
			boolean checkRooms = dictionary.searchRooms(openable);
			boolean checkFurniture = dictionary.searchFurniture(openable);

			if (checkRooms) {
				Connection[] clist = curRoom[0].getConnections();
				for (Connection connection : clist) {
					if (containsExactWord(openable,connection.getName())) {
						if (!connection.isOpen()) {
							if (unlockers!=null) {
								//if user specifies what to use to open the door
								for (String unlocker : unlockers) {
									boolean itemInInv = false;
									for (Item item : inventory) {
										if (containsExactWord(unlocker,item.getName())) {
											itemInInv = true;
											if (connection.useItem(unlocker)) {
												System.out.print("You successfully used the " + unlocker + ".");	
											}
											else {
												System.out.println("You can't use the" + unlocker + " in that way.");
											}
										}
									}
									if (!itemInInv) {
										System.out.println("You look through your backpack, but cannot find the " + unlocker + ".");
									}
								}
								if (connection.getRequirements().size()!=0) {
									System.out.println("\nThis door still needs the following items to open:");
									for (String requirement : connection.getRequirements()) {
										System.out.println(requirement);
									}
								}
								else {
									System.out.println(" The door is now open.");
								}
								return 0;	
							}
							else {
								//if the user doesn't specify, check inventory for required items and use them (PLEASE ADD A CONFIRMATION MESSAGE SO THE USER DOESNT GET RID OF ITEMS THEY WANT FOR SOMETHING ELSE)
								List<String> reqItems = new ArrayList<String>();
								for (Item item : inventory) {
									for (String requirement : connection.getRequirements()) {
										if (item.getName().equalsIgnoreCase(requirement)) {
											reqItems.add(item.getName());
										}
									}
								}
								if (reqItems.size()==0) {
									System.out.println("You don't have the necessary items to open this door. LOOOOOOOSERRRRRR!!!!");
									return 0;
								}
								else {
									System.out.println("The items in your inventory that match the requirements for opening this door are:");
									int i = 0;
									for (String item : reqItems) {
										i++;
										System.out.println("    " + boldBlueColor + i + ": " + resetFormatting + item);
									}
									System.out.print("Would you like to use these items to open the door? [y/n] ");
									String input = scan.nextLine();
									if (input.equalsIgnoreCase("y") || input.equalsIgnoreCase("yes")) {
										for (String item : reqItems) {
											connection.useItem(item);
										}
										System.out.println("You used the items. The door is now open.");
										return 0;
									}
									else {
										System.out.println("You don't use the items. The door remains closed.");
										return 0;
									}
								}
							}
						}
					}
				}
			}
			else {
				//furniture unlocking
				List<Furniture> furnl = curRoom[0].getFurnL();
				for (Furniture furn : furnl) {
					if (containsExactWord(openable, furn.getName())) {
						if (!furn.isOpen()) {
							if (unlockers!=null) {
								//if user specifies what to use to open the furniture
								for (String unlocker : unlockers) {
									boolean itemInInv = false;
									for (Item item : inventory) {
										if (containsExactWord(unlocker,item.getName())) {
											itemInInv = true;
											System.out.println(unlocker);
											if (furn.useItem(unlocker)) {
												System.out.print("You successfully used the " + unlocker + ".");
											}
											else {
												System.out.println("You can't use the " + unlocker + " in that way.");
											}
										}
									}
									if (!itemInInv) {
										System.out.println("You search through your backpack for the " + unlocker + ", but you cannot find it.");
									}
								}
								if (furn.getRequirements().size()!=0) {
									System.out.println("\nThis " + furn.getName() + " still needs the following items to open:");
									for (String requirement : furn.getRequirements()) {
										System.out.println(requirement);
									}
								}
								else {
									System.out.println(" The " + furn.getName() + " is now open.");
								}
								return 0;
							}
							else {
								List<String> reqItems = new ArrayList<String>();
								for (Item item : inventory) {
									for (String requirement : furn.getRequirements()) {
										if (item.getName().equalsIgnoreCase(requirement)) {
											reqItems.add(item.getName());
										}
									}
								}
								if (reqItems.size()==0) {
									System.out.println("You don't have the necessary items to open this " + furn.getName() + ". LOOOOOOOSERRRRRR!!!!");
									return 0;
								}
								else {
									System.out.println("The items in your inventory that match the requirements for opening this " + furn.getName() + " are:");
									int i = 0;
									for (String item : reqItems) {
										i++;
										System.out.println("    " + boldBlueColor + i + ": " + resetFormatting + item);
									}
									System.out.print("Would you like to use these items to open the " + furn.getName() + "? [y/n] ");
									String input = scan.nextLine();
									if (input.equalsIgnoreCase("y") || input.equalsIgnoreCase("yes")) {
										for (String item : reqItems) {
											furn.useItem(item);
										}
										System.out.println("You used the items. the " + furn.getName() + " is now open.");
										return 0;
									}
									else {
										System.out.println("You don't use the items. The " + furn.getName() + " remains closed.");
										return 0;
									}
								}
							}
						}
					}
				}
			}
			System.out.println("Sorry, I'm not quite sure what you're trying to open.");
			return 0;
		};
		commandHashMap.put("open",open);

		//game running
		while (run) {
			//input

			// action
			String action = null;

			// target (usually not items)
			ArrayList<String> targets = new ArrayList<String>();

			// object (will not be room; if action is go or forward or any movement verb, it will use target not object)
			ArrayList<String> objects = new ArrayList<String>();

			System.out.print(greenColor + "> ");
			String input = scan.nextLine();
			// parser logic
			// ############ HI HI YOU NEED TO FIX THIS THE PARSER ISNT PUTTING WORDS LIKE "ke" INTO THE TARGETS/OBJECTS LIST WHICH RETURNS AN ERROR FOR COMMANDS LIKE GRAB
			ArrayList<String> tokenized = new ArrayList<String>(Arrays.asList(input.split(" ")));
			// delete action and all words before action after setting action variable
			int index = -1;
			for (int i=0;i<tokenized.size();i++) {
				String token = tokenized.get(i);
				for (String verb : Dictionary.actions) {
					if (token.equalsIgnoreCase(verb)) {
						action = token.toLowerCase();
						index = i;
						break;
					}
				}
				// will loop until it finds an action verb
				if (index!=-1) break;
			}
			if (action!=null) {
				if (index==-1) {
					// literally isnt possible
					System.out.println("yo how the fuck");
				}
				else {
					// deletes all words before action word and the action word from the tokenized arraylist
					tokenized.subList(0,index+1).clear();
				}
				// remove unneccessary words (highkey forgot how this code works idk man)
				tokenized.removeIf(token ->
						Arrays.stream(Dictionary.useless)
						.anyMatch(u -> u.equalsIgnoreCase(token))
						);
				//remove list words (and and ,)
				tokenized.removeIf(token ->
						Arrays.stream(Dictionary.flags)
						.anyMatch(u -> u.equalsIgnoreCase(token))
						);
				boolean objAndTarg = false;
				// if there is a splitter word, that means there is an object and a target in the sentence
				for (String item : tokenized) {
					System.out.println("item: " + item);
					for (String compare : Dictionary.splitters) {
						if (item.equalsIgnoreCase(compare)) {
							objAndTarg = true;
							tokenized.remove(item);
						}
					}
				}
				// if there is an object and a target
				if (objAndTarg) {
					ArrayList<String> targetList = new ArrayList<String>();
					ArrayList<String> objectList= new ArrayList<String>();
					boolean targl = true;

					for (String item : tokenized) {
						if (Arrays.asList(Dictionary.splitters).contains(item.toLowerCase())) {
							targl=false;
							continue;
						}
						if (targl) {
							targetList.add(item);
						}
						else {
							objectList.add(item);
						}
					}
					if (targetList.size() > 0) {
						targets.addAll(targetList);
					}
					else {
						targets.clear();
					}
					if (objectList.size() > 0) {
						objects.addAll(objectList);
					}
					else {
						objects.clear();
					}
				}
				// if there is only an object or a target
				else {
					boolean checkRooms = false; 
					if (tokenized.size()>0) {
						checkRooms = dictionary.searchRooms(tokenized.get(0).toLowerCase());
					}
					boolean checkItems = false;
					if (tokenized.size()>0) {
						if (dictionary.searchItems(tokenized) == 0) {
							checkItems = false;
						}
						else if (dictionary.searchItems(tokenized) == 1 || dictionary.searchItems(tokenized) == 2) {
							checkItems = true;
						}
					}
					boolean checkFurniture = false;
					if (tokenized.size()>1) {
						checkFurniture = dictionary.searchFurniture(tokenized.get(0).toLowerCase());
					}
					if (checkFurniture && tokenized.size()>1) {
						System.out.println("You can only interact with one piece of furniture at a time.");
						//yea, you tell em!
					}
					System.out.println(checkRooms);
					// think this is where the "ke" bug is
					if (checkRooms) {
						for (String token : tokenized) {
							targets.add(token.toLowerCase());
						}
					}
					else if (checkItems) {
						for (String token : tokenized) {
							objects.add(token.toLowerCase());
						}
					}
					else if (checkFurniture) {
						for (String token : tokenized) {
							targets.add(token.toLowerCase());
						}
					}
					for (String token : tokenized) {
						if (token.equalsIgnoreCase("around")) {
							action = "around";
						}
						else if (token.equalsIgnoreCase("foreward") || token.equalsIgnoreCase("front") || token.equalsIgnoreCase("forewards")) {
							action = "front";
						}
						else if (token.equalsIgnoreCase("backward") || token.equalsIgnoreCase("back") || token.equalsIgnoreCase("backwards")) {
							action = "back";
						}
						else if (token.equalsIgnoreCase("left")) {
							action = "left";
						}
						else if (token.equalsIgnoreCase("right")) {
							action = "right";
						}
						else if (token.equalsIgnoreCase("inventory")) {
							action = "inventory";
						}
						else if (token.equalsIgnoreCase("backpack")) {
							action = "inventory";
						}
						else if (token.equalsIgnoreCase("door")) {
							targets.add("door");
						}
						else if (token.equalsIgnoreCase("backpack")) {
							action = "backpack";
							objects.clear();
							targets.clear();
						}
					}

				}

				//response
				// debug
				System.out.println("action: " + action);
				System.out.println("target(s):");
				for (String target : targets) {
					System.out.print(target + targets.size() + ",");
				}
				System.out.println();
				System.out.println("object(s):");
				for (String object : objects) {
					System.out.print(object + objects.size() + ",");
				}
				System.out.println();
				//command
				System.out.print(resetFormatting);
				if (objects.size()==0) {
					objects=null;
				}
				if (targets.size()==0) {
					targets=null;
				}
				commandHashMap.get(action).command(action,objects,targets);
			}
			else {
				System.out.println(redBackground + "Sorry, not quite sure what \"" + input + "\" means. Try again?" + resetFormatting);
			}
		}
	}
}
