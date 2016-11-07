/*
 * Copyright (C) 2016 Riccardo De Benedictis <riccardo.debenedictis@istc.cnr.it>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package it.cnr.istc.sponsor.view;

import com.microsoft.z3.ArithExpr;
import com.microsoft.z3.IntExpr;
import com.microsoft.z3.Optimize;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Riccardo De Benedictis <riccardo.debenedictis@istc.cnr.it>
 */
public class Problem {

    private final Map<User, Map<Schema, IntExpr>> user_assignments = new IdentityHashMap<>();
    private final Map<Schema, Map<User, IntExpr>> schema_assignments = new IdentityHashMap<>();
    private final com.microsoft.z3.Context ctx;
    private final Optimize o;
    // the objective function..
    private final ArithExpr[] objective;

    public Problem() {
        HashMap<String, String> cfg = new HashMap<>();
        cfg.put("model", "true");
        this.ctx = new com.microsoft.z3.Context(cfg);
        this.o = ctx.mkOptimize();
        for (User user : Context.getInstance().users) {
            user_assignments.put(user, new IdentityHashMap<>());
        }
        for (Schema schema : Context.getInstance().schemas) {
            schema_assignments.put(schema, new IdentityHashMap<>());
        }
        int i = 0, j = 0;
        for (User user : Context.getInstance().users) {
            j = 0;
            for (Schema schema : Context.getInstance().schemas) {
                IntExpr var = ctx.mkIntConst("x_" + i++ + "_" + j++);
                o.Add(ctx.mkGe(var, ctx.mkInt("0")), ctx.mkLe(var, ctx.mkInt("1")));
                user_assignments.get(user).put(schema, var);
                schema_assignments.get(schema).put(user, var);
            }
        }
        this.objective = new ArithExpr[Context.getInstance().users.size() * Context.getInstance().schemas.size()];

        // any users should do something..
        for (User user : Context.getInstance().users) {
            ArithExpr[] cst = new ArithExpr[Context.getInstance().schemas.size()];
            j = 0;
            for (Schema schema : Context.getInstance().schemas) {
                cst[j++] = user_assignments.get(user).get(schema);
            }
            o.Add(ctx.mkGe(ctx.mkAdd(cst), ctx.mkInt("1")));
        }

        // every schema should be done by someone..
        for (Schema schema : Context.getInstance().schemas) {
            ArithExpr[] cst = new ArithExpr[Context.getInstance().users.size()];
            j = 0;
            for (User user : Context.getInstance().users) {
                cst[j++] = user_assignments.get(user).get(schema);
            }
            o.Add(ctx.mkEq(ctx.mkAdd(cst), ctx.mkInt("1")));
        }

        // overlapping schemas cannot be done by the same person..
        Map<LocalDateTime, Collection<Schema>> starting_schemas = new HashMap<>();
        Map<LocalDateTime, Collection<Schema>> ending_schemas = new HashMap<>();
        Set<LocalDateTime> pulses = new HashSet<>();
        for (Schema schema : Context.getInstance().schemas) {
            if (!starting_schemas.containsKey(schema.activity.start.getValue())) {
                starting_schemas.put(schema.activity.start.getValue(), new ArrayList<>());
            }
            if (!ending_schemas.containsKey(schema.activity.end.getValue())) {
                ending_schemas.put(schema.activity.end.getValue(), new ArrayList<>());
            }
            starting_schemas.get(schema.activity.start.getValue()).add(schema);
            ending_schemas.get(schema.activity.end.getValue()).add(schema);
            pulses.add(schema.activity.start.getValue());
            pulses.add(schema.activity.end.getValue());
        }

        // Sort current pulses
        LocalDateTime[] c_pulses_array = pulses.toArray(new LocalDateTime[pulses.size()]);
        Arrays.sort(c_pulses_array);

        List<Schema> overlapping_schemas = new ArrayList<>();
        for (LocalDateTime time : c_pulses_array) {
            if (starting_schemas.containsKey(time)) {
                overlapping_schemas.addAll(starting_schemas.get(time));
            }
            if (ending_schemas.containsKey(time)) {
                overlapping_schemas.removeAll(ending_schemas.get(time));
            }
            if (overlapping_schemas.size() > 1) {
                for (User user : Context.getInstance().users) {
                    ArithExpr[] cst = new ArithExpr[overlapping_schemas.size()];
                    for (Schema schema : overlapping_schemas) {
                        cst[j++] = user_assignments.get(user).get(schema);
                    }
                    o.Add(ctx.mkLe(ctx.mkAdd(cst), ctx.mkInt("1")));
                }
            }
        }

        // We set the user constraints..
        for (User user : Context.getInstance().users) {
            for (Schema schema : user.denials) {
                o.Add(ctx.mkEq(user_assignments.get(user).get(schema), ctx.mkInt("0")));
            }
            for (Schema schema : user.requests) {
                o.Add(ctx.mkEq(user_assignments.get(user).get(schema), ctx.mkInt("1")));
            }
        }
    }
}
