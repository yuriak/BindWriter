package cn.pda.serialport;

import android.R.bool;

public class Tools {

	//byte 转十六进制
		public static String Bytes2HexString(byte[] b, int size) {
		    String ret = "";
		    for (int i = 0; i < size; i++) {
		      String hex = Integer.toHexString(b[i] & 0xFF);
		      if (hex.length() == 1) {
		        hex = "0" + hex;
		      }
		      ret += hex.toUpperCase();
		    }
		    return ret;
		  }
		
		public static byte uniteBytes(byte src0, byte src1) {
		    byte _b0 = Byte.decode("0x" + new String(new byte[]{src0})).byteValue();
		    _b0 = (byte)(_b0 << 4);
		    byte _b1 = Byte.decode("0x" + new String(new byte[]{src1})).byteValue();
		    byte ret = (byte)(_b0 ^ _b1);
		    return ret;
		  }
		
		//十六进制转byte
		public static byte[] HexString2Bytes(String src) {
			int len = src.length() / 2;
			byte[] ret = new byte[len];
			byte[] tmp = src.getBytes();

			for (int i = 0; i < len; i++) {
				ret[i] = uniteBytes(tmp[i * 2], tmp[i * 2 + 1]);
			}
			return ret;
		}
		
		/* byte[]转Int */
		public static int bytesToInt(byte[] bytes)
		{
			int addr = bytes[0] & 0xFF;
			addr |= ((bytes[1] << 8) & 0xFF00);
			addr |= ((bytes[2] << 16) & 0xFF0000);
			addr |= ((bytes[3] << 25) & 0xFF000000);
			return addr;

		}
		
		/* Int转byte[] */
		public static byte[] intToByte(int i)
		{
			byte[] abyte0 = new byte[4];
			abyte0[0] = (byte) (0xff & i);
			abyte0[1] = (byte) ((0xff00 & i) >> 8);
			abyte0[2] = (byte) ((0xff0000 & i) >> 16);
			abyte0[3] = (byte) ((0xff000000 & i) >> 24);
			return abyte0;
		}
		
		public static String byteTohexStringExceptZero(byte[] bytes){
			String string = "";
		    for (int i = 0; i < bytes.length; i++) {
		      String hex = Integer.toHexString(bytes[i] & 0xFF);
		      if (hex.length() == 1) {
		        hex = "0" + hex;
		      }
		      string += hex.toUpperCase();
		    }
			if (string.length()==0) {
				return "-1";
			}
			boolean firstNoneZero=false;
			for (int i = 0; i <string.length(); i++) {
				if (string.charAt(i)!='0') {
					return string.substring(i);
				}
			}
			return "0";
		}
		
		public static byte[] bandNumberToByte(int n){
			String number=String.valueOf(n);
			StringBuilder builder=new StringBuilder();
			for (int i = 0; i < 32; i++) {
				if (32-i>number.length()) {
					builder.append("0");
				}else {
					builder.append(number.substring(number.length()-(32-i),number.length()-(32-i)+1));
				}
			}
			byte[] result=HexString2Bytes(builder.toString());
			return result;
		}
		
		public static Byte[] b2B(byte[] b){
			Byte[] bytes=new Byte[b.length];
			for (int i = 0; i < b.length; i++) {
				bytes[i]=new Byte(b[i]);
			}
			return bytes;
		}
}
