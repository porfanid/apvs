package ch.cern.atlas.apvs.dosimeter.server;

import junit.framework.Assert;

import org.junit.Test;

public class DosimeterTest {

	@Test
	public void encodeTest() {
		Assert.assertEquals("088842000000009000000000000000000000000000009E", Dosimeter.encode(new Dosimeter(88842,  9, 0)));
		Assert.assertEquals("08903700000005700000000000000000000000000000BC", Dosimeter.encode(new Dosimeter(89037, 57, 0)));
		Assert.assertEquals("08903700000005700003000000000000000000000000B9", Dosimeter.encode(new Dosimeter(89037, 57, 3)));
	
		
	}

	@Test
	public void decodeTest() {
		Assert.assertEquals(new Dosimeter(88842,  9, 0), Dosimeter.decode("08884202000000950D000020C4AF0001000000000000F6"));
		Assert.assertEquals(new Dosimeter(89037, 57, 0), Dosimeter.decode("08903702000005740D00002064A700010000000000007D"));
		Assert.assertEquals(new Dosimeter(89037, 57, 3), Dosimeter.decode("08903702000005740D03002064A700010000000000007A"));

		Assert.assertEquals(new Dosimeter(89037, 57, 3), Dosimeter.decode("08903700000005700003000000000000000000000000B9"));
   }
}
