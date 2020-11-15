/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author user
 */
public class Packet {

    private int seqNum;
    private byte[] msg;
    private boolean exist;

    public Packet(int seqNum, byte[] msg, boolean exist) {
        this.seqNum = seqNum;
        this.msg = msg;
        this.exist = exist;
    }

    public void setSeqNum(int seqNum) {
        this.seqNum = seqNum;
    }

    public int getSeqNum() {
        return this.seqNum;
    }

    public void setMsg(byte[] msg) {
        this.msg = msg;
    }

    public byte[] getMsg() {
        return this.msg;
    }

    //does this packet exist or is it empty due to loss
    public void setExist() {
        this.exist = exist;
    }

    public boolean getExist() {
        return this.exist;
    }

    public String toString() {
        return "Sequence Number: + " + getSeqNum() + "\nMsg: " + getMsg()
                + "\nExist: " + getExist();
    }
}
