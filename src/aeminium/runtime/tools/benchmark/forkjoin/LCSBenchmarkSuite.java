/**
 * Copyright (c) 2010-11 The AEminium Project (see AUTHORS file)
 * 
 * This file is part of Plaid Programming Language.
 *
 * Plaid Programming Language is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 *  Plaid Programming Language is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Plaid Programming Language.  If not, see <http://www.gnu.org/licenses/>.
 */

package aeminium.runtime.tools.benchmark.forkjoin;

import aeminium.runtime.examples.fjtests.AeminiumLCS;
import aeminium.runtime.Runtime;
import aeminium.runtime.implementations.Factory;
import aeminium.runtime.tools.benchmark.forkjoin.implementations.LCS;

public class LCSBenchmarkSuite {
	
	Benchmark[] tests;
	
	protected int BLOCKSIZE = 100;
	String s1 = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Suspendisse euismod orci sed eros aliquam in feugiat enim bibendum. In hac habitasse platea dictumst. Praesent vitae diam diam. Morbi fermentum metus vel odio sodales vitae pellentesque quam elementum. Integer nec ipsum eget eros ornare ornare. Donec in nunc est. Etiam non dolor lacus. Sed eget aliquet urna. Nulla facilisi. Sed id justo nulla, a tempus lorem. Praesent tempus feugiat arcu ut consectetur. Phasellus quam mi, posuere sit amet dictum nec, blandit in ante. Duis ut bibendum nisl. Vestibulum hendrerit luctus metus id ultricies. Duis et massa eu elit pharetra bibendum. Pellentesque eu nunc magna, sit amet imperdiet nunc. Proin dui sem, aliquet adipiscing consequat vitae, sagittis nec justo. Duis varius enim eu risus lobortis tristique. Sed ipsum elit, luctus vel lobortis eget, volutpat at lorem. Praesent tincidunt justo condimentum justo mattis rutrum.Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Nunc iaculis tincidunt neque, ac varius enim consectetur eget. Nulla consectetur magna non tellus sagittis ac porta est accumsan. In hac habitasse platea dictumst. Quisque eu placerat quam. Aliquam dignissim fermentum purus at tincidunt. Pellentesque et ante est. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Proin neque urna, eleifend sit amet tincidunt vel, placerat eu leo. Donec ultrices, velit ac hendrerit tempor, nunc mauris ultricies eros, ac tristique neque augue at sapien. Duis ac augue enim, sed euismod neque. Sed leo ante, tristique eu facilisis id, fringilla cursus velit. Proin egestas, purus nec fringilla elementum, tellus felis bibendum magna, eu bibendum urna velit sed tellus. Integer non diam sit amet ipsum adipiscing tempus. Praesent tellus sapien, mattis vitae imperdiet in, auctor id orci. Nullam nisi velit, condimentum in condimentum euismod, tempus et dui. Curabitur convallis suscipit felis vel sollicitudin. Vestibulum at augue sed augue auctor ornare a fermentum elit. Integer vitae quam id leo placerat pulvinar nec eu diam. Donec tincidunt, ante vel fringilla placerat, ante orci bibendum mauris, sed interdum lectus magna sed tellus. Phasellus auctor adipiscing sapien nec sagittis. Duis mattis, nisi nec pulvinar pellentesque, risus nibh accumsan magna, nec ornare dui justo et neque. Fusce mi odio, eleifend vel vestibulum id, molestie eget dolor. Vestibulum vel leo vel nisl tempus egestas. In nisl nisi, feugiat nec varius id, dapibus in nunc. Duis cursus neque nec augue sollicitudin nec consequat augue dapibus. Maecenas dignissim tempor augue, vel volutpat diam pellentesque non. Sed eu est vel magna dignissim accumsan pellentesque non massa. Nullam cursus nisi ac sem feugiat faucibus sodales sapien lobortis. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Mauris eleifend tincidunt risus at dapibus. Aenean consequat iaculis sapien a semper. Vivamus felis tortor, imperdiet vitae rutrum at, semper eu odio. In hac habitasse platea dictumst. Suspendisse ipsum magna, pretium sed commodo id, imperdiet gravida libero. Nullam eget metus neque, a posuere velit. Aliquam interdum, lacus id consectetur bibendum, orci nunc tempus libero, non facilisis turpis urna sed velit. Maecenas mi elit, facilisis nec vestibulum at, varius a dui. Etiam consequat commodo mauris convallis tincidunt. Etiam non lectus nisi. Sed felis mauris, vehicula vitae molestie vel, scelerisque at orci. Phasellus viverra nulla ut sem ullamcorper fermentum semper metus sagittis. Aenean dictum, enim vitae volutpat molestie, sem felis tristique erat, a pretium est lectus a orci. Quisque et leo dui, eu sodales ante. Ut eleifend sollicitudin bibendum. Curabitur ligula risus, mattis a tincidunt ac, aliquet volutpat justo. Vivamus erat purus, auctor vel lobortis non, posuere eu justo. Integer justo augue, commodo sed tincidunt nec, ultricies elementum odio. Curabitur vehicula, odio non elementum pulvinar, nibh nisl pellentesque neque, euismod adipiscing nunc arcu eu risus. Duis dignissim, orci eget interdum ultricies, risus turpis faucibus nunc, quis gravida lectus mauris sed mauris. Fusce nibh augue, vestibulum quis lobortis at, sollicitudin in nisi. Ut nec turpis vel libero pretium aliquam. Ut rhoncus mollis lacus, non facilisis arcu hendrerit adipiscing. Nunc at nunc justo. Nunc erat sem, accumsan at eleifend id, adipiscing sit amet nisl. Cras nec elit nec enim hendrerit mattis. Etiam sapien diam, malesuada vitae bibendum eget, adipiscing nec massa. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Nulla quis tellus ante, eu ultrices diam. Suspendisse non mauris non ipsum gravida adipiscing. Vivamus vel est nec eros suscipit convallis id id erat. Donec quam turpis, elementum eu imperdiet sed, placerat porta lacus. Mauris quam est, placerat id interdum a, varius eu quam. Vestibulum eu augue in nisl adipiscing molestie sed nec nibh. Nulla hendrerit elementum eros, vitae dignissim enim varius vitae. Cras enim urna, fringilla id ultricies faucibus, venenatis sit amet enim. Nulla facilisi. Vestibulum accumsan faucibus tortor in suscipit. Integer luctus luctus volutpat. Aliquam ac leo dictum massa consectetur ultricies sed et mauris. Suspendisse potenti. In sit amet urna diam. Cras tincidunt nisl vitae orci accumsan vestibulum. Donec vel dolor sapien. Sed tincidunt ornare enim et consequat. Fusce lacinia malesuada suscipit. Vivamus quis ante ac ipsum varius semper at nec tortor. Donec auctor tincidunt sem a ornare. Donec vel purus id turpis cursus tincidunt at id sem. In hac habitasse platea dictumst. Donec pulvinar lobortis justo eu varius. Donec magna lectus, rhoncus vitae sodales non, lacinia at nisl. Etiam elementum mollis ligula, ut placerat diam varius et. Nullam id arcu ac augue molestie malesuada. Etiam sem nibh, faucibus sed vehicula sed, ullamcorper a eros. Sed lacinia lacinia posuere. Curabitur feugiat cursus risus vel euismod. Aliquam scelerisque nulla sed dui aliquam auctor. Ut eros nibh, ultricies ut eleifend ut, condimentum a eros. Aenean et nisi nisl. Cras magna leo, sollicitudin at pharetra laoreet, posuere tristique eros. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Vivamus magna tellus, malesuada vel commodo id, venenatis nec nulla. Integer vel libero sit amet tortor sollicitudin ultrices. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Duis sagittis, dolor non mattis bibendum, magna diam feugiat odio, ut blandit eros arcu eu lacus. Curabitur porta dolor et massa consequat eget.";
	String s2 = "Their honour precarious, their liberty provisional, lasting only until the discovery of their crime; their position unstable, like that of the poet who one day was feasted at every table, applauded in every theatre in London, and on the next was driven from every lodging, unable to find a pillow upon which to lay his head, turning the mill like Samson and saying like him: ?he two sexes shall die, each in a place apart!? excluded even, save on the days of general disaster when the majority rally round the victim as the Jews rallied round Dreyfus, from the sympathy?t times from the society?f their fellows, in whom they inspire only disgust at seeing themselves as they are, portrayed in a mirror which, ceasing to flatter them, accentuates every blemish that they have refused to observe in themselves, and makes them understand that what they have been calling their love (a thing to which, playing upon the word, they have by association annexed all that poetry, painting, music, chivalry, asceticism have contrived to add to love) springs not from an ideal of beauty which they have chosen but from an incurable malady; like the Jews again (save some who will associate only with others of their race and have always on their lips ritual words and consecrated pleasantries), shunning one another, seeking out those who are most directly their opposite, who do not desire their company, pardoning their rebuffs, moved to ecstasy by their condescension; but also brought into the company of their own kind by the ostracism that strikes them, the opprobrium under which they have fallen, having finally been invested, by a persecution similar to that of Israel, with the physical and moral characteristics of a race, sometimes beautiful, often hideous, finding (in spite of all the mockery with which he who, more closely blended with, better assimilated to the opposing race, is relatively, in appearance, the least inverted, heaps upon him who has remained more so) a relief in frequenting the society of their kind, and even some corroboration of their own life, so much so that, while steadfastly denying that they are a race (the name of which is the vilest of insults), those who succeed in concealing the fact that they belong to it they readily unmask, with a view less to injuring them, though they have no scruple about that, than to excusing themselves; and, going in search (as a doctor seeks cases of appendicitis) of cases of inversion in history, taking pleasure in recalling that Socrates was one of themselves, as the Israelites claim that Jesus was one of them, without reflecting that there were no abnormals when homosexuality was the norm, no anti-Christians before Christ, that the disgrace alone makes the crime because it has allowed to survive only those who remained obdurate to every warning, to every example, to every punishment, by virtue of an innate disposition so peculiar that it is more repugnant to other men (even though it may be accompanied by exalted moral qualities) than certain other vices which exclude those qualities, such as theft, cruelty, breach of faith, vices better understood and so more readily excused by the generality of men; forming a freemasonry far more extensive, more powerful and less suspected than that of the Lodges, for it rests upon an identity of tastes, needs, habits, dangers, apprenticeship, knowledge, traffic, glossary, and one in which the members themselves, who intend not to know one another, recognise one another immediately by natural or conventional, involuntary or deliberate signs which indicate one of his congeners to the beggar in the street, in the great nobleman whose carriage door he is shutting, to the father in the suitor for his daughter? hand, to him who has sought healing, absolution, defence, in the doctor, the priest, the barrister to whom he has had recourse; all of them obliged to protect their own secret but having their part in a secret shared with the others, which the rest of humanity does not suspect and which means that to them the most wildly improbable tales of adventure seem true, for in this romantic, anachronistic life the ambassador is a bosom friend of the felon, the prince, with a certain independence of action with which his aristocratic breeding has furnished him, and which the trembling little cit would lack, on leaving the duchess? party goes off to confer in private with the hooligan; a reprobate part of the human whole, but an important part, suspected where it does not exist, flaunting itself, insolent and unpunished, where its existence is never guessed; numbering its adherents everywhere, among the people, in the army, in the church, in the prison, on the throne; living, in short, at least to a great extent, in a playful and perilous intimacy with the men of the other race, provoking them, playing with them by speaking of its vice as of something alien to it; a game that is rendered easy by the blindness or duplicity of the others, a game that may be kept up for years until the day of the scandal, on which these lion-tamers are devoured; until then, obliged to make a secret of their lives, to turn away their eyes from the things on which they would naturally fasten them, to fasten them upon those from which they would naturally turn away, to change the gender of many of the words in their vocabulary, a social constraint, slight in comparison with the inward constraint which their vice, or what is improperly so called, imposes upon them with regard not so much now to others as to themselves, and in such a way that to themselves it does not appear a vice";
	
	
	public LCSBenchmarkSuite() {
		tests = new Benchmark[3];
		s1 = s1 + s1 + s1;
		
		tests[0] = new Benchmark() {
			@Override
			public String getName() {
				return "Sequential LCS";
			}
			
			@Override
			public long run() {
				LCS gen = new LCS(BLOCKSIZE);
				long start = System.nanoTime();
				gen.seqCompute(s1, s2);
				long end = System.nanoTime();
				
				return end-start;
			}
			
		};
		
		tests[1] = new Benchmark() {
			
			@Override
			public String getName() {
				return "ThreadExecutor LCS";
			}
			
			@Override
			public long run() {
				LCS gen = new LCS(BLOCKSIZE);
				long start = System.nanoTime();
				gen.parCompute(s1, s2);
				long end = System.nanoTime();
				
				return end-start;
			}
		};
		
		tests[2] = new Benchmark() {
			
			Runtime rt = Factory.getRuntime();
			
			@Override
			public String getName() {
				return "Aeminium LCS";
			}
			
			@Override
			public long run() {

				rt.init();
				AeminiumLCS gen = new AeminiumLCS(BLOCKSIZE);
				
				long start = System.nanoTime();
				gen.compute(rt, s1, s2);
				
				rt.shutdown();
				long end = System.nanoTime();
				return end-start;
			}
		};
		
	}
	
	
	public static void main(String[] args) {
		LCSBenchmarkSuite suite = new LCSBenchmarkSuite();
		new BenchmarkExecutor(suite.getTests()).run(args);
	}
	
	public Benchmark[] getTests() {
		return tests;
	}

}
