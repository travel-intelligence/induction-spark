package induction 

import scala.io._
import com.amadeus.ti.models.lift.TicketLiftModel._
import com.amadeus.ti.parsers.lift._
import scala.math

object useTicket {
  
	def main( args: Array[String] ) {	
		// get input file
		val defaultFile = "/home/juarele/scala/learning/Induction_step1/LIFT.txt" // default
		val file = if (args.length == 1) args(0) else defaultFile // if user writes in stdin
		this.readFile(file)
	}
	
	def readFile( file: String) {
		// from the standard library, we read the lines in file, 
	    // and make the output iterable
		val lines = Source.fromFile(file).getLines.toIterable
		
		// send the result to getTktLiftObjfunction from object TicketLiftSrcToObj. 
		// It returns a ticket following Model Lift:
		// case class TicketLift(header: Header, tickets: Seq[Ticket], trailer: Trailer)
				
		TicketLiftSrcToObj.getTktLiftObj(lines) match {
		  case None => {
		    //(0,Map(),Map()) // returned value
		    println("Empty ticket lift model")
		    }	
		  case Some(ticketList) => {
		    
		    // list of tickets filtered by those that have actually flown 
			val flownTickets = ticketList
		    	.flatMap(_.tickets)
		    	.filter(_.usage.usageType == "F")
		    
		    println("Number of tickets that actually flown: "+
		        flownTickets.length)
	    	
		    // create a list with origins and destinations
        	// first, we access to seq[TicketOriginDestination], 
		    // and then we combine the two 'origin' and 'destination' 
	        // lists into a Seq[(origin,destination)] using zip
		    val onds = flownTickets.map(_.usage.oAndD)
		    val ondsList = onds.map(_.usageOrigin) zip 		    			   	 
		    			   onds.map(_.usageDestination)
		    // 10 top OnDs
		    // Note the minus sign when counting the number of equal OnDs. 
	    	// I use this trick because sortBy sorts in ascending order. 
			// There might be a faster way of sorting elements in descending 
		    // order, for instance, by dropping all the elements in the list but  
	    	// the last n elements; but for this we'd need to know the number of 
	    	// elements.  
		    val topOndsList = ondsList.groupBy(x=>x).mapValues(- _.size).
		    	toList.sortBy(_._2).take(10)

	    	println("Top 10 OnDs in number of passengers")
		    topOndsList.foreach{ x =>
		      println("OnD: "+x._1.toString()+", pax: "+(-x._2).toString()) 
			}
		    
		    val cost = flownTickets.		
		    		   map(_.salesTicketDetails.ticketDocumentAmount)
		    
		    // combine cost list with OnD list
		    val costOnDList = (onds zip cost).map(x=>
		      			  ((x._1.usageOrigin,
		      			   x._1.usageDestination),
		      			   x._2.toDouble))
		    val topCostOnDList = costOnDList.
		             groupBy(x=>x._1).
		             map{
		    		 case (k,v) => (k,-v.map(_._2).sum)
		             }.
		             toList.sortBy(_._2).take(10)
		    
	    	println("Top 10 OnDs in revenue")
		    topCostOnDList.foreach{ x =>
		      println("OnD: "+x._1.toString()+", revenue: "+
		          (-(math rint x._2*100)/100).toString()) 
			}
		    
		  }
		}		
	}
}

