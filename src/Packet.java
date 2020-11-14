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

    public Packet(int seqNum, byte[] msg) {
        this.seqNum = seqNum;
        this.msg = msg;
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

    public String toString() {
        return "Sequence Number: + " + this.seqNum + "\nMsg: " + this.msg;
    }
}
