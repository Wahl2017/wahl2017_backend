package at.wahl2017.backend.model;

import java.util.ArrayList;

public class Scenario {

	public static Election build2017Scenario() {
		Election e = new Election();
	
		e.parties = new ArrayList<>();
		
		{ // SPÖ
			Candidate frontRunner = new Candidate("Christian", "Kern");
			frontRunner.setTitle("Mag.");
			frontRunner.setPressPicture("https://flickr.com/photos/sozialdemokratie/31763441355/in/album-72157677974421766/");
			frontRunner.setWebsite("https://spoe.at/person/bundeskanzler-christian-kern");
			frontRunner.setTwitterHandle("kernchri");
			
			Party party = new Party("SPÖ", "Sozialdemokratische Partei Österreichs");
			party.addCandidate(frontRunner);
			party.addKeywords("VSStÖ");
			
			e.parties.add(party);
		}
		
		{ // ÖVP
			Candidate frontRunner = new Candidate("Sebastian", "Kurz");
			frontRunner.setPressPicture("https://d9hhrg4mnvzow.cloudfront.net/www.sebastian-kurz.at/cv/de-1/ae900157-backgrounds44.jpg");
			frontRunner.setWebsite("http://www.sebastian-kurz.at");
			frontRunner.setTwitterHandle("sebastiankurz");
			
			Party party = new Party("ÖVP", "Österreichische Volkspartei");
			party.addCandidate(frontRunner);
			party.addKeywords("JVP");
			
			e.parties.add(party);
		}
		
		{ // FPÖ
			Candidate frontRunner = new Candidate("Heinz-Christian", "Strache");
			frontRunner.setTitle("Mag.");
			frontRunner.setPressPicture("http://www.hcstrache.at/fileadmin/user_upload/www.hcstrache.at/images/hcstrache1.png");
			frontRunner.setWebsite("http://www.hcstrache.at");
			frontRunner.setTwitterHandle("HCStracheFP");
			
			Party party = new Party("FPÖ", "Freiheitliche Partei Österreich");
			party.addCandidate(frontRunner);
			party.addKeywords("HC");
			
			e.parties.add(party);
		}
		
		return e;
	}

}
