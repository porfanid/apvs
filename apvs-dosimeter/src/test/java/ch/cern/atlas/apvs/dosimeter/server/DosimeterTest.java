package ch.cern.atlas.apvs.dosimeter.server;

import junit.framework.Assert;

import org.junit.Test;

import ch.cern.atlas.apvs.domain.Dosimeter;


public class DosimeterTest {

	@Test
	public void encodeTest() {
		Assert.assertEquals("088842000000009000000000000000000000000000009E", DosimeterCoder.encode(new Dosimeter(88842,  9, 0, null)));
		Assert.assertEquals("08903700000005700000000000000000000000000000BC", DosimeterCoder.encode(new Dosimeter(89037, 57, 0, null)));
		Assert.assertEquals("08903700000005700003000000000000000000000000B9", DosimeterCoder.encode(new Dosimeter(89037, 57, 3, null)));
	
		
	}

	@Test
	public void decodeTest() {
		Assert.assertEquals(new Dosimeter(88842,  9, 0, null), DosimeterCoder.decode("08884202000000950D000020C4AF0001000000000000F6", null));
		Assert.assertEquals(new Dosimeter(89037, 57, 0, null), DosimeterCoder.decode("08903702000005740D00002064A700010000000000007D", null));
		Assert.assertEquals(new Dosimeter(89037, 57, 3, null), DosimeterCoder.decode("08903702000005740D03002064A700010000000000007A", null));

		Assert.assertEquals(new Dosimeter(89037, 57, 3, null), DosimeterCoder.decode("08903700000005700003000000000000000000000000B9", null));
   }
}
