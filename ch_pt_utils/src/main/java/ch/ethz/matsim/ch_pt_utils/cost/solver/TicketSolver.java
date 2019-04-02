package ch.ethz.matsim.ch_pt_utils.cost.solver;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.gnu.glpk.GLPK;
import org.gnu.glpk.GLPKConstants;
import org.gnu.glpk.SWIGTYPE_p_double;
import org.gnu.glpk.SWIGTYPE_p_int;
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

	public Result solve(int numberOfStages, Collection<Ticket> inputTickets) {
		if (inputTickets.size() == 0) {
			return new Result();
		}

		List<Ticket> tickets = new LinkedList<>(inputTickets);

		// Set up problem
		// Columns is number of tickets, rows is number of stages
		glp_prob problem = GLPK.glp_create_prob();
		GLPK.glp_add_cols(problem, tickets.size());
		GLPK.glp_add_rows(problem, numberOfStages);
		GLPK.glp_java_set_msg_lvl(GLPKConstants.GLP_JAVA_MSG_LVL_OFF);
		GLPK.glp_term_out(GLPKConstants.GLP_OFF);

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

		// GLPK.glp_write_lp(problem, new glp_cpxcp(), "/home/sebastian/problem");

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
				//System.out.println("Simplex return value: " + simplexReturnValue);
				//System.out.println("MIP return value: " + mipReturnValue);

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
	}
}
