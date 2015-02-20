package shi_p1;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class shi_p1 {
	protected class Instruction {
		protected int cs;
		protected char ci;
		protected int ts;
		protected char ti;
		protected char d;

		protected Instruction(int cs, char ci, int ts, char ti, char d){
			this.cs = cs;
			this.ci = ci;
			this.ts = ts;
			this.ti = ti;
			this.d = d;
		}
	}

	private ArrayList<Integer> accept = new ArrayList<Integer>();
	private ArrayList<Integer> reject = new ArrayList<Integer>();
	private int start;
	private char[] alphabet;
	private ArrayList<Instruction> instruction = new ArrayList<Instruction>();
	private int currentState;

	public shi_p1(String alphabet) {
		this.alphabet = alphabet.toCharArray();
	}

	private void addAccept(int i){
		this.accept.add(i);
	}

	private void addReject(int i){
		this.reject.add(i);
	}

	private void setStart(int i){
		this.start = i;
	}

	private void addInstruction(int cs, char ci, int ts, char ti, char d){
		this.instruction.add(new Instruction(cs, ci, ts, ti, d));
	}

	private Instruction getInstruction(int cs, char ci){
		for(Instruction instruction : this.instruction){
			if(instruction.cs == cs && instruction.ci == ci){
				return instruction;
			}
		}
		return null;
	}

	private String run(){
		int counter = 0;
		int index = 0;
		this.currentState = this.start;
		StringBuilder result = new StringBuilder();
		result.append(start);
		while (true){
			if(counter > 5000){
				result.append(" quit");
				break;
			}
			if(this.accept.contains(this.currentState)){
				result.append(" accept");
				break;
			}
			if(this.reject.contains(this.currentState)){
				result.append(" reject");
				break;
			}
			try {
				Instruction currentInstruction = getInstruction(currentState, this.alphabet[index]);
				if(currentInstruction != null){
					currentState = currentInstruction.ts;
					result.append("->"+currentState);
					this.alphabet[index] = currentInstruction.ti;
					if(currentInstruction.d == 'R' || currentInstruction.d == 'r'){
						index++;
					} else if (currentInstruction.d == 'L' || currentInstruction.d == 'l'){
						index--;
					} else {
						throw new Exception("Transition function error.");
					}
				} else {
					throw new Exception("Transition function error.");
				}
			} catch (ArrayIndexOutOfBoundsException e){
				result.append(" bounds");
				break;
			} catch (Exception e){
				System.out.println(e.getMessage());
				break;
			}
			counter ++;
		}
		return result.toString();
	}

	public static void main(String[] args){
		//System.out.println(args[0]+"\n"+args[1]);
		if(args[0] == null || args[0].isEmpty() || args[1] == null || args[1].isEmpty()){
			System.out.println("Please provide machine definition and input alphabet.");
		} else {
			shi_p1 machine = new shi_p1(args[1]);
			Scanner input;
			try{
				input = new Scanner(new File(args[0]));
				while(input.hasNextLine()){
					String[] rawInstruc = input.nextLine().split("\t");
					if(rawInstruc[0].equalsIgnoreCase("state")){
						try{
							if(rawInstruc[2].equalsIgnoreCase("accept")){
								machine.addAccept(Integer.parseInt(rawInstruc[1]));
							} else if(rawInstruc[2].equalsIgnoreCase("reject")){
								machine.addReject(Integer.parseInt(rawInstruc[1]));
							} else if(rawInstruc[2].equalsIgnoreCase("start")){
								machine.setStart(Integer.parseInt(rawInstruc[1]));
							} else {
								System.out.println("State definition error.");
							}
						} catch (NumberFormatException e){
							System.out.println("State definition error. Can't parse.");
						}
					} else if (rawInstruc[0].equals("transition")){
						if(rawInstruc[1].matches("\\d+") && rawInstruc[3].matches("\\d+") 
								&& rawInstruc[2].matches("[^\\s]") && rawInstruc[4].matches("[^\\s]") 
								&& rawInstruc[5].matches("[RLrl]")){
							machine.addInstruction(Integer.parseInt(rawInstruc[1]),
									rawInstruc[2].charAt(0), Integer.parseInt(rawInstruc[3]), 
									rawInstruc[4].charAt(0), rawInstruc[5].charAt(0));
						} else {
							System.out.println("Transition definition error.");
						}
					} else {
						System.out.println("Definition file is not readable.");
					}
				}
				input.close();
				String result = machine.run();
				System.out.println(result);
			} catch (FileNotFoundException e){
				System.out.println("Unable to read the file.");
			}
		}
	}
}