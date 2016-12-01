package com.autopet.hardware.aidl.imxserial;

import java.text.SimpleDateFormat;

import android.util.Log;

/**
 * @author Mr Chen
 *
 */
public class ComBean {
		public byte[] bRec=null;
		public String sRecTime="";
		public ComBean(byte[] buffer,int size){
			bRec=new byte[size];
			for (int i = 0; i < size; i++)
			{
				bRec[i]=buffer[i];
			}
			SimpleDateFormat sDateFormat = new SimpleDateFormat("hh:mm:ss");       
			sRecTime = sDateFormat.format(new java.util.Date()); 
		}
}