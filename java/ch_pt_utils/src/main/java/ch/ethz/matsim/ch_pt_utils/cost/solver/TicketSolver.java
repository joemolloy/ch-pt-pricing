package ch.ethz.matsim.ch_pt_utils.cost.solver;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.gnu.glpk.GLPK;
import org.gnu.glpk.GLPKConstants;
import org.gnu.glpk.SWIGTYPE_p_double;
import org.gnu.glpk.SWIGTYPE_p_int;
import org.gnu.glpk.glp_cpxcp;
import org.gnu.glpk.glp_iocp;
import org.gnu.glpk.glp_prob;
import org.gnu.glpk.glp_smcp;

public class TicketSolver {
	static public class Result {
		public final boolean isValid;
		public final Collection<Ticket> tickets;
		public final double price;

		Result() {
			price = Double.NaN;
			isValid = false;
			tickets = Collections.emptyList();
		}

		Result(Collection<Ticket> tickets, double price) {
			this.price = price;
			this.tickets = tickets;
			this.isValid = true;
		}
	}

	public Result solve(int numberOfStages, List<Ticket> inputTickets) {
		List<Ticket> tickets = new LinkedList<>(inputTickets);

		// Set up problem
		// Columns is number of tickets, rows is number of stages
		glp_prob problem = GLPK.glp_create_prob();
		GLPK.glp_add_cols(problem, tickets.size());
		GLPK.glp_add_rows(problem, numberOfStages);

		for (int i = 1; i <= tickets.size(); i++) {
			// All variables are binary variables
			GLPK.glp_set_col_kind(problem, i, GLPKConstants.GLP_BV);
		}

		SWIGTYPE_p_int indices;
		SWIGTYPE_p_double values;

		// Add constraints
		for (int j = 1; j <= numberOfStages; j++) {
			indices = GLPK.new_intArray(tickets.size() + 1);
			values = GLPK.new_doubleArray(tickets.size() + 1);

			GLPK.glp_set_row_bnds(problem, j, GLPKConstants.GLP_LO, 1.0, 0.0);

			for (int i = 1; i <= tickets.size(); i++) {
				GLPK.intArray_setitem(indices, i, i);

				if (tickets.get(i - 1).getCoverage().get(j - 1)) {
					GLPK.doubleArray_setitem(values, i, 1.0);
				} else {
					GLPK.doubleArray_setitem(values, i, 0.0);
				}
			}

			GLPK.glp_set_mat_row(problem, j, tickets.size(), indices, values);
		}

		// Objective
		GLPK.glp_set_obj_dir(problem, GLPKConstants.GLP_MIN);

		for (int i = 1; i <= tickets.size(); i++) {
			GLPK.glp_set_obj_coef(problem, i, tickets.get(i - 1).getPrice());
		}

		GLPK.glp_write_lp(problem, new glp_cpxcp(), "/home/sebastian/problem");

		// LP Solution
		glp_smcp simplexParameters = new glp_smcp();
		GLPK.glp_init_smcp(simplexParameters);
		int simplexReturnValue = GLPK.glp_simplex(problem, simplexParameters);

		if (simplexReturnValue == 0) {
			// MIP Solution
			glp_iocp parameters = new glp_iocp();
			GLPK.glp_init_iocp(parameters);
			int mipReturnValue = GLPK.glp_intopt(problem, parameters);

			if (mipReturnValue == 0) {
				System.out.println("Simplex return value: " + simplexReturnValue);
				System.out.println("MIP return value: " + mipReturnValue);

				double price = GLPK.glp_mip_obj_val(problem);

				List<Ticket> resultTickets = new LinkedList<>();

				for (int i = 1; i <= tickets.size(); i++) {
					if (GLPK.glp_mip_col_val(problem, i) > 0.0) {
						resultTickets.add(tickets.get(i - 1));
					}
				}

				return new Result(resultTickets, price);
			}
		}

		return new Result();

		/*
		 * for (int rowIndex = 1; rowIndex < numberOfStages + 1; rowIndex++) {
		 * GLPK.glp_set_row_name(problem, rowIndex, String.format("Stage %d",
		 * rowIndex)); GLPK.glp_set_row_bnds(problem, rowIndex, GLPKConstants.GLP_LO, 0,
		 * 0);
		 * 
		 * SWIGTYPE_p_int indices = GLPK.new_intArray(tickets.size()); SWIGTYPE_p_double
		 * values = GLPK.new_doubleArray(tickets.size());
		 * 
		 * for (int i = 1; i <= tickets.size(); i++) { GLPK.intArray_setitem(indices, i,
		 * i);
		 * 
		 * if (tickets.get(i - 1).getCoverage().get(rowIndex - 1)) {
		 * GLPK.doubleArray_setitem(values, i, 1.0); } else {
		 * GLPK.doubleArray_setitem(values, i, 0.0); } }
		 * 
		 * GLPK.glp_set_mat_row(problem, rowIndex, numberOfStages, indices, values); }
		 * 
		 * System.out.println("HERE2");
		 * 
		 * GLPK.glp_set_obj_name(problem, "price"); GLPK.glp_set_obj_dir(problem,
		 * GLPKConstants.GLP_MIN);
		 * 
		 * for (int columnIndex = 1; columnIndex <= tickets.size(); columnIndex++) {
		 * GLPK.glp_set_obj_coef(problem, columnIndex, tickets.get(columnIndex -
		 * 1).getPrice()); }
		 * 
		 * System.out.println("HERE3");
		 */

		/*
		 * glp_smcp parameters = new glp_smcp(); GLPK.glp_init_smcp(parameters); int
		 * returnValue = GLPK.glp_simplex(problem, parameters);
		 * 
		 * GLPK.glp_write_prob(problem, 0, "/home/sebastian/problem");
		 * 
		 * System.out.println(returnValue);
		 */
	}

	/*
	 * static public void main2(String[] args) { Ticket ticket; List<Ticket> tickets
	 * = new LinkedList<>();
	 * 
	 * // Ticket(Single ZVV (110), Price 4.40 [X ]) // // Ticket(Day ZVV (110),
	 * Price 8.80 [X ]) // // Ticket(Single Libero (100), Price 4.60 [ ]) // //
	 * Ticket(Day Libero (100), Price 13.00 [ ]) // // Ticket(SBB 8503000, 8501008,
	 * Price 89.00 [ X]) // // Ticket(SBB 8503008, 8503000, 8501008, Price 93.00
	 * [XX])
	 * 
	 * ticket = new Ticket(2, 4.40, "Single ZVV (110)");
	 * ticket.getCoverage().set(0); tickets.add(ticket);
	 * 
	 * ticket = new Ticket(2, 8.80, "Day ZVV (110)"); ticket.getCoverage().set(0);
	 * tickets.add(ticket);
	 * 
	 * ticket = new Ticket(2, 4.40, "Single Libero (100)"); tickets.add(ticket);
	 * 
	 * ticket = new Ticket(2, 8.80, "Day Libero (100)"); tickets.add(ticket);
	 * 
	 * ticket = new Ticket(2, 89.00, "SBB 8503000, 8501008");
	 * ticket.getCoverage().set(1); tickets.add(ticket);
	 * 
	 * ticket = new Ticket(2, 93.00, "SBB 8503008, 8503000, 8501008");
	 * ticket.getCoverage().set(0); ticket.getCoverage().set(1);
	 * tickets.add(ticket);
	 * 
	 * TicketSolver solver = new TicketSolver(); solver.solve(2, tickets); }
	 */

	/*
	 * static public void main(String[] args) { main2(args); System.exit(1);
	 * 
	 * glp_prob problem = GLPK.glp_create_prob();
	 * 
	 * GLPK.glp_add_cols(problem, 2); GLPK.glp_add_rows(problem, 3);
	 * 
	 * GLPK.glp_set_col_kind(problem, 1, GLPKConstants.GLP_IV);
	 * GLPK.glp_set_col_kind(problem, 2, GLPKConstants.GLP_IV);
	 * 
	 * GLPK.glp_set_col_bnds(problem, 1, GLPKConstants.GLP_FR, 0, 0);
	 * GLPK.glp_set_col_bnds(problem, 2, GLPKConstants.GLP_FR, 0, 0);
	 * 
	 * SWIGTYPE_p_int indices; SWIGTYPE_p_double values;
	 * 
	 * // Constraint 1
	 * 
	 * indices = GLPK.new_intArray(1); values = GLPK.new_doubleArray(1);
	 * 
	 * GLPK.glp_set_row_bnds(problem, 1, GLPKConstants.GLP_LO, 4.2, 0);
	 * GLPK.intArray_setitem(indices, 1, 1); GLPK.doubleArray_setitem(values, 1,
	 * 1.0); GLPK.glp_set_mat_row(problem, 1, 1, indices, values);
	 * 
	 * // Constraint 2
	 * 
	 * indices = GLPK.new_intArray(1); values = GLPK.new_doubleArray(1);
	 * 
	 * GLPK.glp_set_row_bnds(problem, 2, GLPKConstants.GLP_LO, 2, 0);
	 * GLPK.intArray_setitem(indices, 1, 2); GLPK.doubleArray_setitem(values, 1,
	 * 1.0); GLPK.glp_set_mat_row(problem, 2, 1, indices, values);
	 * 
	 * // Constraint 32
	 * 
	 * indices = GLPK.new_intArray(2); values = GLPK.new_doubleArray(2);
	 * 
	 * GLPK.glp_set_row_bnds(problem, 3, GLPKConstants.GLP_LO, 5, 0);
	 * GLPK.intArray_setitem(indices, 1, 1); GLPK.intArray_setitem(indices, 2, 2);
	 * GLPK.doubleArray_setitem(values, 2, 3.0); GLPK.doubleArray_setitem(values, 1,
	 * 4.0); GLPK.glp_set_mat_row(problem, 3, 2, indices, values);
	 * 
	 * // Objective
	 * 
	 * GLPK.glp_set_obj_dir(problem, GLPKConstants.GLP_MIN);
	 * GLPK.glp_set_obj_coef(problem, 1, 1.0); GLPK.glp_set_obj_coef(problem, 2,
	 * 1.0);
	 * 
	 * // LP Solution glp_smcp simplexParameters = new glp_smcp();
	 * GLPK.glp_init_smcp(simplexParameters); int simplexReturnValue =
	 * GLPK.glp_simplex(problem, simplexParameters);
	 * 
	 * // MIP Solution glp_iocp parameters = new glp_iocp();
	 * GLPK.glp_init_iocp(parameters); int mipReturnValue = GLPK.glp_intopt(problem,
	 * parameters);
	 * 
	 * glp_cpxcp cp = new glp_cpxcp();
	 * 
	 * GLPK.glp_write_lp(problem, cp, "/home/sebastian/problem");
	 * System.out.println("Simplex return value: " + simplexReturnValue);
	 * System.out.println("MIP return value: " + mipReturnValue);
	 * 
	 * double objective = GLPK.glp_mip_obj_val(problem);
	 * System.out.println(objective); }
	 */
}
