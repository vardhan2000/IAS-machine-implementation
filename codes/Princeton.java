public class Princeton {

    private String AC, MAR, MBR, IR, IBR, MQ, PC = "000000000000";
    private String[] memory = new String[1000];
    
    // left_flag set to true only when the previous instruction was a 
    // jump_left(conditional or unconditional). Its value is checked only
    // when one of the four kinds of jump instructions is encountered. "loop_flag"
    // turns false when a "halt" instruction is encountered
    private boolean left_flag, loop_flag = true;
    
    // jump_count is set to zero only when one of the
    // four kinds of jump instructions is executed 
    private static int jump_count = -1;

    // constructor
    Princeton(){ 

    }

    /* main() method */
    public static void main(String[] args){

        // creating a Princeton object named IAS1
        Princeton IAS1 = new Princeton();
        String X1, X2, X3, X4;
        String initial_val = String.format("%40d", 0).replaceAll(" ", "0");
        
        /* Preprogramming the memory of IAS1 */
        IAS1.AC = IAS1.MQ = initial_val; // initializing the value of AC and MQ of IAS1
        IAS1.memory[500] = Princeton.intToBinary(3);
        IAS1.memory[501] = Princeton.intToBinary(2);
        IAS1.memory[502] = Princeton.intToBinary(-6);
        
        
        X1 = Princeton.intToBinary(500).substring(28);//3
        X2 = Princeton.intToBinary(501).substring(28);//2
        X3 = Princeton.intToBinary(600).substring(28);//5
        X4 = Princeton.intToBinary(502).substring(28);//-6
        
        /* Given below are some random set of IAS instructions which make use
        of almost all the 21 instructions available in a Princeton computer */
        
        // loadm(x) and add(x)
        IAS1.memory[0] = "00000001" + X1 + "00000101" + X2;
        // stor_m(x) and load_mq_m(x)
        IAS1.memory[1] = "00100001" + X3 + "00001001" + X2;
        // mul_m(x) and load_mq
        IAS1.memory[2] = "00001011" + X1 + "00001010" + X2;
        // load -m(x3) and jump plus left
        IAS1.memory[3] = "00000010" + X3 + "00001111" + "000000001001";
        // div m(x2) and jump plus right
        IAS1.memory[4] = "00001100" + X2 + "00010000" + "000000001100";
        // halt and load m(x4)
        IAS1.memory[12] = "00000000" + X1 + "00000001" + X4;
        // sub m(x4) and load mod m(x4)
        IAS1.memory[13] = "00000110" + X4 + "00000011" + X4;
        // lsh and rsh
        IAS1.memory[14] = "00010100" + String.format("%12s", "") + "00010101" + X1;
        // stor left m(1) and stor right m(13) {address modify instructions}
        IAS1.memory[15] = "00010010" + "000000000001" + "00010011" + "000000001101";
        // jump left and halt
        IAS1.memory[16] = "00001101" + "000000010100" + "00000000" + X3;
        // sub mod m(x4) and add mod m(x4)
        IAS1.memory[20] = "00001000" + X4 +"00000011" + X4;
        // null and halt
        IAS1.memory[21] = String.format("%20s", "") + "00000000" + X2;

        System.out.println("\n 1st program \n");
        // fetch, decode, execute and writeback
        IAS1.InstructionCycles();

        System.out.println("\n 2nd program \n");

        // creating a Princeton object named IAS2
        Princeton IAS2 = new Princeton();
        IAS2.AC = IAS2.MQ = initial_val;

        /* Preprogramming the memory of IAS2 */
        IAS2.memory[500] = Princeton.intToBinary(5);// a = 5
        IAS2.memory[501] = Princeton.intToBinary(15);// b = 15;

        // X1 = Princeton.intToBinary(500).substring(28);// a
        // X2 = Princeton.intToBinary(501).substring(28);// b
        // X3 = Princeton.intToBinary(600).substring(28);// c
        
        /* the following IAS instructions are corresponding to the code:-
        a = 5, b = 15;
        if(a >= b)
            c = a;
        else
            c = b;
         */
        // load m(x1) and sub m(x2)
        IAS2.memory[0] = "00000001" + X1 + "00000110" + X2;
        // if jump left and jump right 
        IAS2.memory[1] = "00001111" + "000000000100" + "00001110" + "000000000101";
        // load m(x1) and stor m(x3)
        IAS2.memory[4] = "00000001" + X1 + "00100001" + X3;
        // halt and load m(x2)
        IAS2.memory[5] = "00000000" + X3 + "00000001" + X2;
        // stor m(x3) and halt
        IAS2.memory[6] = "00100001" + X3 + "00000000" + "000000000000";

        // fetch, decode, execute and writeback
        IAS2.InstructionCycles();

        System.out.println("\na = "+ IAS2.memory[500] + "\nb = " + IAS2.memory[501]
                            + "\nc = " + IAS2.memory[600]);
        
    } // main() ends
        
    
    // decode, execute and writeback cycles take place only when 
    // the "controller()" method is called. Rest of the lines in 
    // "InstructionCycles()" execute the fetch cycle
    public void InstructionCycles(){

        // the instruction cycle will end(i.e. the while loop will break) only 
        // when it faces a "halt" instruction, whose opcode is "00000000"
        while(this.loop_flag){
            this.MAR = this.PC; // MAR <- PC
            
            // if previous instruction was a jump_right(conditional or unconditional)
            if(Princeton.jump_count == 0 && !this.left_flag){
                this.MBR = String.format("%20s", "") + this.memory[Princeton.binaryToInt(this.MAR)].substring(20);
            
            // if previous instruction was either not a jump
            // or a jump_left(conditional or unconditional)
            } else {
                this.MBR = this.memory[Princeton.binaryToInt(this.MAR)]; // MBR <- memory[MAR]
            }
            
            // if there is an lhs instruction
            if(!this.MBR.substring(0,2).equals("  ")){
                this.IBR = this.MBR.substring(20); // IBR <- MBR[20:39]
                this.IR = this.MBR.substring(0,8); // IR <- MBR[0:7]
                this.MAR = this.MBR.substring(8,20); // MAR <- MBR[8:19] 

                this.controller(this.IR);

                if(!this.loop_flag){
                    break;
                }

                // if lhs is NOT a jump(any of the 4 jumps)
                if(Princeton.jump_count != 0){
                    this.IR = this.IBR.substring(0,8); // IR <- IBR[0:7]
                    this.MAR = this.IBR.substring(8); // MAR <- IBR[8:19]
                    
                    this.controller(this.IR);
                }

                if(!this.loop_flag){
                    break;
                }

            // if NO lhs instruction
            } else {
                this.IR = this.MBR.substring(20,28); // IR <- MBR[20:27]
                this.MAR = this.MBR.substring(28); // MAR <- MBR[28:39]

                this.controller(this.IR);

                if(!this.loop_flag){
                    break;
                }
            }

            // updating the value of PC to (PC+1) if currently executed 
            // instruction was NOT a jump(any of the 4 kinds) 
            if(Princeton.jump_count != 0){
                int val_pc = Princeton.binaryToInt(this.PC);
                val_pc += 1;
                this.PC = Princeton.intToBinary(val_pc).substring(28);
            }

            // printing the values in different registers 
            System.out.println("PC  = " + this.PC);
            System.out.println("MAR = " + this.MAR);
            System.out.println("IR  = " + this.IR);
            System.out.println("IBR = " + this.IBR);
            System.out.println("AC  = " + this.AC);
            System.out.println("MQ  = " + this.MQ);
            System.out.println("MBR = " + this.MBR + "\n");
            
        }
    } // InstructionCycles() ends
    
    // converts an int to 40 bit signed binary
    public static String intToBinary(int x){
        if(x >= 0){
            return String.format("%40s", Integer.toBinaryString(x)).replaceAll(" ", "0");
        } else {
            return "1" + String.format("%39s", Integer.toBinaryString(x * (-1))).replaceAll(" ", "0");
        }
    }

    // converts 40 bit signed binary to an int 
    public static int binaryToInt(String binary){
        int dec = Integer.parseInt(binary.substring(1), 2);
        
        if(binary.substring(0,1).equals("0")){
            return dec;
        } return dec * (-1);
    }

    // takes care of decode, execute and writeback phases
    public void controller(String opcode){
        
        // switch() with 22 case statements, 21 basic instructions
        // and an additive "halt" instruction 
        switch(opcode){
            case "00001010":
                this.LOAD_MQ();
                if(Princeton.jump_count == -1){
                    Princeton.jump_count = 1;
                } else {
                    Princeton.jump_count += 1;
                }
                break;
            case "00001001":
                this.LOAD_MQ_M(this.MAR);
                if(Princeton.jump_count == -1){
                    Princeton.jump_count = 1;
                } else {
                    Princeton.jump_count += 1;
                }
                break;
            case "00100001":
                this.STOR_M(this.MAR);
                if(Princeton.jump_count == -1){
                    Princeton.jump_count = 1;
                } else {
                    Princeton.jump_count += 1;
                }
                break;
            case "00000001":
                this.LOAD_M(this.MAR);
                if(Princeton.jump_count == -1){
                    Princeton.jump_count = 1;
                } else {
                    Princeton.jump_count += 1;
                }
                break;
            case "00000010":
                LOAD_minus_M(this.MAR);
                if(Princeton.jump_count == -1){
                    Princeton.jump_count = 1;
                } else {
                    Princeton.jump_count += 1;
                }
                break;
            case "00000011":
                this.LOAD_mod_M(this.MAR);
                if(Princeton.jump_count == -1){
                    Princeton.jump_count = 1;
                } else {
                    Princeton.jump_count += 1;
                }
                break;
            case "00000100":
                this.LOAD_minus_mod_M(this.MAR);
                if(Princeton.jump_count == -1){
                    Princeton.jump_count = 1;
                } else {
                    Princeton.jump_count += 1;
                }
                break;
            case "00001101":
                this.JUMP_left_M(this.MAR);
                break;
            case "00001110":
                this.JUMP_right_M(this.MAR);
                break;
            case "00001111":
                this.JUMP_plus_left_M(this.MAR);
                break;
            case "00010000":
                this.JUMP_plus_right_M(this.MAR);
                break;
            case "00000101":
                this.ADD_M(this.MAR);
                if(Princeton.jump_count == -1){
                    Princeton.jump_count = 1;
                } else {
                    Princeton.jump_count += 1;
                }
                break;
            case "00000111":
                this.ADD_mod_M(this.MAR);
                if(Princeton.jump_count == -1){
                    Princeton.jump_count = 1;
                } else {
                    Princeton.jump_count += 1;
                }
                break;
            case "00000110":
                this.SUB_M(this.MAR);
                if(Princeton.jump_count == -1){
                    Princeton.jump_count = 1;
                } else {
                    Princeton.jump_count += 1;
                }
                break;
            case "00001000":
                this.SUB_mod_M(this.MAR);
                if(Princeton.jump_count == -1){
                    Princeton.jump_count = 1;
                } else {
                    Princeton.jump_count += 1;
                }
                break;
            case "00001011":
                this.MUL_M(this.MAR);
                if(Princeton.jump_count == -1){
                    Princeton.jump_count = 1;
                } else {
                    Princeton.jump_count += 1;
                }
                break;
            case "00001100":
                this.DIV_M(this.MAR);
                if(Princeton.jump_count == -1){
                    Princeton.jump_count = 1;
                } else {
                    Princeton.jump_count += 1;
                }
                break;
            case "00010100":
                this.LSH();;
                if(Princeton.jump_count == -1){
                    Princeton.jump_count = 1;
                } else {
                    Princeton.jump_count += 1;
                }
                break;
            case "00010101":
                this.RSH();;
                if(Princeton.jump_count == -1){
                    Princeton.jump_count = 1;
                } else {
                    Princeton.jump_count += 1;
                }
                break;
            case "00010010":
                this.STOR_left_M(this.MAR);
                if(Princeton.jump_count == -1){
                    Princeton.jump_count = 1;
                } else {
                    Princeton.jump_count += 1;
                }
                break;
            case "00010011":
                this.STOR_right_M(this.MAR);
                if(Princeton.jump_count == -1){
                    Princeton.jump_count = 1;
                } else {
                    Princeton.jump_count += 1;
                }
                break;
            case "00000000":
                this.loop_flag = false;
                if(Princeton.jump_count == -1){
                    Princeton.jump_count = 1;
                } else {
                    Princeton.jump_count += 1;
                }
                break;
            default:
                break;
        }
    } // controller() ends

    
    /* Following are the 21 methods for the 21 IAS instructions */

    public void LOAD_MQ(){
        this.AC = this.MQ;
    }

    public void LOAD_MQ_M(String X){
        this.MBR = this.memory[Princeton.binaryToInt(X)];
        this.MQ = this.MBR;
    }

    public void STOR_M(String X){
        this.MBR = this.AC;
        this.memory[Princeton.binaryToInt(X)] = this.MBR;
    }

    public void LOAD_M(String X){
        this.MBR = this.memory[Princeton.binaryToInt(X)]; 
        this.AC = this.MBR;
    }

    public void LOAD_minus_M(String X){
        this.MBR = this.memory[Princeton.binaryToInt(X)];
        int temp = (-1) * Princeton.binaryToInt(this.MBR); // done by ALU
        this.AC = Princeton.intToBinary(temp); 
    }

    // the functionality of this method is executed by ALU
    public int mod_M(String X){
        this.MBR = this.memory[Princeton.binaryToInt(X)];
        int temp = Princeton.binaryToInt(this.MBR);
        if(temp >= 0){
            return temp;
        } return temp * (-1);
    }

    public void LOAD_mod_M(String X){
        this.AC = Princeton.intToBinary(mod_M(X));
    }

    public void LOAD_minus_mod_M(String X){
        this.AC = Princeton.intToBinary(this.mod_M(X) * (-1)); 
    }

    public void JUMP_left_M(String X){
        this.PC = X;
        this.left_flag = true;
        Princeton.jump_count = 0;
    }

    public void JUMP_right_M(String X){
        this.PC = X;
        this.left_flag = false;
        Princeton.jump_count = 0;
    }

    public void JUMP_plus_left_M(String X){
        if(Princeton.binaryToInt(this.AC) >= 0){
            this.JUMP_left_M(X);   
        }
    }

    public void JUMP_plus_right_M(String X){
        if(Princeton.binaryToInt(this.AC) >= 0){
            this.JUMP_right_M(X);
        }
    }

    public void ADD_M(String X){
        int val_mbr, val_ac;
        this.MBR = this.memory[Princeton.binaryToInt(X)];
        val_mbr = Princeton.binaryToInt(this.MBR);
        val_ac = Princeton.binaryToInt(this.AC);
        
        val_ac = val_ac + val_mbr; // done by ALU
        this.AC = Princeton.intToBinary(val_ac); 
    }

    public void ADD_mod_M(String X){
        int val_ac = Princeton.binaryToInt(this.AC);
        val_ac = val_ac + this.mod_M(X);
        this.AC = Princeton.intToBinary(val_ac); 
    }

    public void SUB_M(String X){
        int val_mbr, val_ac;
        this.MBR = this.memory[Princeton.binaryToInt(X)];
        val_mbr = Princeton.binaryToInt(this.MBR);
        val_ac = Princeton.binaryToInt(this.AC);
        
        val_ac = val_ac - val_mbr;// done by ALU
        this.AC = Princeton.intToBinary(val_ac); 
    }

    public void SUB_mod_M(String X){
        int val_ac = Princeton.binaryToInt(this.AC);
        val_ac = val_ac - this.mod_M(X);// done by ALU
        this.AC = Princeton.intToBinary(val_ac);
    }

    public void MUL_M(String X){
        int val_mbr, val_mq, val;
        String Val;
        this.MBR = this.memory[Princeton.binaryToInt(X)];
        val_mbr = Princeton.binaryToInt(this.MBR);
        val_mq = Princeton.binaryToInt(this.MQ);
        
        // done by ALU
        val = val_mq * val_mbr;
        if(val >= 0){
            Val = String.format("%80s", Integer.toBinaryString(val)).replaceAll(" ", "0");
        } else {
            Val = "1" + String.format("%79s", Integer.toBinaryString(val * (-1))).replaceAll(" ", "0");
        }
        
        this.AC = Val.substring(0,40);
        this.MQ = Val.substring(40);
    }

    public void DIV_M(String X){
        int val_mbr, val_ac, val_mq = 0;
        this.MBR = this.memory[Princeton.binaryToInt(X)];
        val_mbr = Princeton.binaryToInt(this.MBR);
        val_ac = Princeton.binaryToInt(this.AC);
        
        if(val_mbr != 0){
            if(val_ac/val_mbr < 0 && val_ac%val_mbr !=0){ // done by ALU
                val_mq = val_ac/val_mbr;
                val_mq -= 1;
                val_ac = val_ac - (val_mbr * val_mq);
            } else {// done by ALU
                val_mq = val_ac / val_mbr;
                val_ac = val_ac % val_mbr;
            }
            this.AC = Princeton.intToBinary(val_ac);
            this.MQ = Princeton.intToBinary(val_mq);
        }
    }

    public void LSH(){
        int val_ac = Princeton.binaryToInt(this.AC);
        val_ac = val_ac * 2;// done by ALU
        this.AC = Princeton.intToBinary(val_ac);
    }

    public void RSH(){
        int val_ac = Princeton.binaryToInt(this.AC);
        val_ac = val_ac / 2;// done by ALU
        this.AC = Princeton.intToBinary(val_ac);
    }

    public void STOR_left_M(String X){
        int index = binaryToInt(X);
        this.MBR = this.AC.substring(28);
        this.memory[index] = this.memory[index].substring(0,8) + this.MBR + this.memory[index].substring(20);
    }

    public void STOR_right_M(String X){
        int index = binaryToInt(X);
        this.MBR = this.AC.substring(28);
        this.memory[index] = this.memory[index].substring(0,28) + this.MBR;
    }
}