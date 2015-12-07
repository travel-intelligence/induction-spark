package induction 

import scala.io._
import com.amadeus.ti.models.lift.TicketLiftModel._
import com.amadeus.ti.parsers.lift._
import scala.math

object useTicket {
  
	def main( args: Array[String] ) {	
		// get input file
		val defaultFile = "LIFT.txt" // default
		val file = if (args.length == 1) args(0) else defaultFile // if user writes in stdin
		this.readFile(file)
	}
	
	def readFile( file: String) {
		// from the standard library, we read the lines in file, 
	    // and make the output iterable
		val lines = Source.fromFile(file).getLines.toIterable
		
		/*
		 * 
		 send the result to getTktLiftObjfunction from object TicketLiftSrcToObj. 
		 It returns a ticket following Model Lift:
		    TicketLift(header: Header, tickets: Seq[Ticket], trailer: Trailer)
		 * 
		 */
				
		TicketLiftSrcToObj.getTktLiftObj(lines) match {
		  case None => {
		    println("Empty ticket lift model")
		    }	
		  case Some(ticketList) => {
		    
		    // list of tickets filtered by those that have actually flown 
			val flownTickets = ticketList
		    	.flatMap(_.tickets)
		    	.filter(_.usage.usageType == "F")
		    
		    println("Number of tickets that actually flown: "+
		        flownTickets.length)
	    	
		    /*
		     *  
		     Top 10 OnDs by number of passengers
		     *
		     create a list with origins and destinations
        	 first, access to seq[TicketOriginDestination], 
		     and then combine the two 'origin' and 'destination' 
	         lists into a Seq[(origin,destination)] using zip	         
	        */
		    val onds = flownTickets.map(_.usage.oAndD)		    
		    // Obtain a list of tuples: Seq[(Origi,Destin)]
		    val ondsList = onds.map(_.usageOrigin) zip 		    			   	 
		    			   onds.map(_.usageDestination)
		    			   
		    /*		     
		     In the following sentence, I count the number of pax.
		     Note the minus sign when counting the number of equal OnDs. 
	    	 I use this trick because sortBy sorts in ascending order. 
			 There might be a faster way of sorting elements in descending 
		     order, for instance, by dropping every element but  
	    	 the last n terms; but for this we'd need to know the number of 
	    	 elements.
	    	 * 
	    	 */  
		    val topOndsList = ondsList. // from Seq[(Origi,Destin)]
		    			      groupBy(x=>x). // group by (Origi,Destin)
		    			      mapValues(- _.size). // count number of element for each OnD
		    			      toList.sortBy(_._2).take(10) // list, sort and take first 10

	    	println("Top 10 OnDs in number of passengers")
		    topOndsList.foreach{ x =>
		      println("OnD: "+x._1.toString()+", pax: "+(-x._2).toString()) 
			}
		    
			/*
			 Top 10  OnDs in revenue
			 */
			
			// Seq with revenue per fligth
		    val cost = flownTickets.		
		    		   map(_.salesTicketDetails.ticketDocumentAmount)
		    
		    // Obtain a Seq[((Origi,Destin), cost)]
		    val costOnDList = (onds zip cost). // combine cost list with OnD list
		    			      map(x=> // give the desired format: ((Origi,Destin), cost)
		      			        ((x._1.usageOrigin,
		      			        x._1.usageDestination),
		      			        x._2.toDouble)
		      			      )
	        // Top 10
		    val topCostOnDList = costOnDList. // from Seq[((Origi,Destin), cost)]
		             groupBy(x=>x._1).        // group by first tuple (Origi,Destin)
		             map{					  // sum revenues over each OnD
		               case (k,v) => (k,-v.map(_._2).sum)
		             }.
		             toList.sortBy(_._2).take(10) // list, sort and keep only 
		             							  // first 10 elements
		    
	    	println("Top 10 OnDs in revenue")
		    topCostOnDList.foreach{ x =>
		      println("OnD: "+x._1.toString()+", revenue: "+
		          (-(math rint x._2*100)/100).toString()) 
			}
		    
		  }
		}		
	}
}

