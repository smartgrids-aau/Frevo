package evosynch;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import utils.NESRandom;

/**
 * The Class NodeLinker is used to link the Nodes of EvoSynch. It can either
 * link the nodes with bidirectional links or unidirectional links.
 * 
 * @author Thomas Dittrich
 * 
 */
public class NodeLinker {

	private static boolean isNeighborhoodset(
			ArrayList<ArrayList<Node>> numlinkneighbors,
			boolean bidirectionallinks) {
		boolean isNeighborhoodset = true;
		for (int i = 0; i < numlinkneighbors.size() - 1; i++) {
			if (!numlinkneighbors.get(i).isEmpty()) {
				isNeighborhoodset = false;
			}
		}
		if (bidirectionallinks
				&& numlinkneighbors.get(numlinkneighbors.size() - 1).size() > 1) {
			isNeighborhoodset = false;
		}
		if (!bidirectionallinks
				&& !numlinkneighbors.get(numlinkneighbors.size() - 1).isEmpty()) {
			isNeighborhoodset = false;
		}
		return isNeighborhoodset;
	}

	/**
	 * Creates the links between the nodes
	 * 
	 * @param nodes
	 *            the nodes that should be linked
	 * @param numLinks
	 *            number of links per node (if the links are unidirectional then
	 *            this is the number of output links)
	 * @param bidirectionallinks
	 *            links are bidirectional or unidirectional
	 */
	public static void linkNodes(ArrayList<Node> nodes, int numLinks,
			boolean bidirectionallinks) {
		int numNodes = nodes.size();
		Queue<Node> nextnodestoconnect = new LinkedList<Node>();
		ArrayList<ArrayList<Node>> nodesgroupedbylinknum = new ArrayList<ArrayList<Node>>(
				numLinks);
		ArrayList<Node> unlinkednodes = new ArrayList<Node>(numNodes);
		for (int i = 0; i < numLinks; i++) {
			nodesgroupedbylinknum.add(new ArrayList<Node>(numNodes));
		}
		for (Node n : nodes) {
			unlinkednodes.add(n);
			if (numLinks > 0) {
				nodesgroupedbylinknum.get(0).add(n);
			}
		}
		if (numLinks > 0) {
			nextnodestoconnect.offer(nodes.get(0));

			int numtrials = 0;
			try {
				while (!isNeighborhoodset(nodesgroupedbylinknum,
						bidirectionallinks)) {

					Node node1 = nextnodestoconnect.peek();
					Node node2 = null;

					node2 = getsecondnode(nodes, bidirectionallinks,
							nodesgroupedbylinknum, unlinkednodes, node1);

					// check if there is already a link between these two nodes
					boolean linkexists = node1.connectedNodes.contains(node2);
					if (linkexists && numtrials <= 5) { // if there is already a
														// link then try again
														// for 5 times to find
														// a second node
						numtrials++;
					} else {
						if (numtrials > 5) {
							//System.out
							//		.println("link exists: " + node1.toString()
							//				+ " " + node2.toString());
						}
						numtrials = 0;

						// create link
						node1.connectNode(node2);
						if (bidirectionallinks) {
							node2.connectNode(node1);
						}

						unlinkednodes.remove(node1);
						unlinkednodes.remove(node2);

						// add the second node to the list of nodes, which
						// should be connected next
						if (!nextnodestoconnect.contains(node2)
								&& node2.connectedNodes.size() < numLinks) {
							nextnodestoconnect.offer(node2);
						} else
						// remove the second node from the list of nodes, which
						// should be connected next (because it has already
						// enough links. This can only happen with bidirectional
						// links)
						if (nextnodestoconnect.contains(node2)
								&& node2.connectedNodes.size() >= numLinks) {
							nextnodestoconnect.remove(node2);
						}

						// move the first node to next list because its number
						// of links has increased
						nodesgroupedbylinknum.get(
								node1.connectedNodes.size() - 1).remove(node1);

						if (node1.connectedNodes.size() < numLinks) {
							nodesgroupedbylinknum.get(
									node1.connectedNodes.size()).add(node1);
						}

						if (bidirectionallinks) {
							// move the first node to next list because its
							// number
							// of links has increased
							nodesgroupedbylinknum.get(
									node2.connectedNodes.size() - 1)

							.remove(node2);
							if (node2.connectedNodes.size() < numLinks) {
								nodesgroupedbylinknum.get(
										node2.connectedNodes.size()).add(node2);
							}
						}
					}

					// remove the first node from the list of nodes, which
					// should be connected next (because it has already enough
					// links)
					if (node1.connectedNodes.size() >= numLinks) {
						nextnodestoconnect.poll();
					}
				}
			} catch (Error e) {
				e.printStackTrace();
			}
		}
	}

	private static NESRandom r;

	private static Node getsecondnode(ArrayList<Node> nodes,
			boolean bidirectionallinks,
			ArrayList<ArrayList<Node>> nodesgroupedbylinknum,
			ArrayList<Node> unlinkednodes, Node node1) throws Error {
		if (r == null)
			r = new NESRandom();
		Node node2 = null;
		if (!bidirectionallinks) {
			node2 = getsecondnodeunidirectional(nodes, unlinkednodes, node1);
		} else
			node2 = getsecondnodebidirectional(nodesgroupedbylinknum, node1);
		return node2;
	}

	private static Node getsecondnodebidirectional(
			ArrayList<ArrayList<Node>> nodesgroupedbylinknum, Node node1)
			throws Error {
		Node node2 = null;
		int ileastconnectedlist = 0;
		while (nodesgroupedbylinknum.get(ileastconnectedlist).isEmpty()) {
			ileastconnectedlist++;
		}
		if (node1.connectedNodes.size() == ileastconnectedlist
				&& nodesgroupedbylinknum.get(ileastconnectedlist).size() > 1) {
			int j = r.nextInt(nodesgroupedbylinknum.get(ileastconnectedlist)
					.size() - 1);
			if (j >= nodesgroupedbylinknum.get(ileastconnectedlist).indexOf(
					node1)) {
				j++;
			}
			node2 = nodesgroupedbylinknum.get(ileastconnectedlist).get(j);
		} else if (node1.connectedNodes.size() == ileastconnectedlist) {

			for (int i = ileastconnectedlist + 1; i < nodesgroupedbylinknum
					.size() && node2 == null; i++)
				if (nodesgroupedbylinknum.get(i).size() > 0) {
					int j = r.nextInt(nodesgroupedbylinknum.get(
							ileastconnectedlist + 1).size());
					node2 = nodesgroupedbylinknum.get(ileastconnectedlist + 1)
							.get(j);
				}
			if (node2 == null) {
				throw new Error("was not able to find a second neighbor");
			}

		} else {
			int j = r.nextInt(nodesgroupedbylinknum.get(ileastconnectedlist)
					.size());
			node2 = nodesgroupedbylinknum.get(ileastconnectedlist).get(j);
		}
		return node2;
	}

	private static Node getsecondnodeunidirectional(ArrayList<Node> nodes,
			ArrayList<Node> unlinkednodes, Node node1) {
		Node node2;
		if (!unlinkednodes.isEmpty()) {
			int j = 0;
			if (unlinkednodes.contains(node1)) {
				j = r.nextInt(unlinkednodes.size() - 1);
				if (j >= unlinkednodes.indexOf(node1)) {
					j++;
				}
			} else {
				j = r.nextInt(unlinkednodes.size());
			}
			node2 = unlinkednodes.get(j);
		} else {
			int j = 0;

			j = r.nextInt(nodes.size() - 1);
			if (j >= nodes.indexOf(node1)) {
				j++;
			}
			node2 = nodes.get(j);
		}
		return node2;
	}
}
