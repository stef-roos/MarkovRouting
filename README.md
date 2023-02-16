# MarkovRouting
Modelling p2p routing performance using markov chains

Code for paper Roos, S., Salah, H., & Strufe, T. (2015). 'Determining the hop count in kademlia-type systems.', ICCCN 2015 for the mathematical model 

three packages:
util: some stochastics needed for the model
kadtype: routing model for various kademlia versions
attack: models for attacks 

main logic is implemented in class kadtype/KadType, most other classes just fix the parameters of certain Kademlia versions

evaluation in kadtype/Tests, public static void testPerformance(String[] args) allows running one of 18 settings considered in the study 


