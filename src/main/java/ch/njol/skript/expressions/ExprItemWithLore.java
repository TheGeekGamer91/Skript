/**
 * This file is part of Skript.
 *
 * Skript is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Skript is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Skript.  If not, see <http://www.gnu.org/licenses/>.
 *
 *
 * Copyright 2011-2017 Peter Güttinger and contributors
 */
package ch.njol.skript.expressions;

import java.util.Arrays;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.inventory.meta.ItemMeta;
import org.eclipse.jdt.annotation.Nullable;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import ch.njol.util.StringUtils;

@Name("Item with Lore")
@Description({"Returns the given item type with the specified lore added to it.",
		"If multiple strings are passed, each of them will be a separate line in the lore."})
@Examples({"set {_test} to stone with lore \"line 1\" and \"line 2\"",
		"give {_test} to player"})
@Since("INSERT VERSION")
public class ExprItemWithLore extends PropertyExpression<ItemType, ItemType> {

	static {
		Skript.registerExpression(ExprItemWithLore.class, ItemType.class, ExpressionType.PROPERTY,
				"%itemtypes% with [(a|the)] lore %strings%");
	}

	@SuppressWarnings("null")
	private Expression<String> lore;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean kleenean, ParseResult parseResult) {
		setExpr((Expression<ItemType>) exprs[0]);
		lore = (Expression<String>) exprs[1];
		return true;
	}

	@Override
	protected ItemType[] get(Event e, ItemType[] itemTypes) {
		String[] lore = this.lore.getArray(e);
		if (lore == null)
			return new ItemType[0];
		return Stream.of(itemTypes).map(itemType -> {
			itemType = itemType.clone();
			ItemMeta meta = (ItemMeta) itemType.getItemMeta();
			if (meta == null) {
				meta = Bukkit.getItemFactory().getItemMeta(Material.STONE);
			}
			meta.setLore(Arrays.asList(StringUtils.join(lore, "\n").split("\n")));
			itemType.setItemMeta(meta);
			return itemType;
		}).toArray(ItemType[]::new);
	}


	@Override
	public Class<? extends ItemType> getReturnType() {
		return ItemType.class;
	}

	@Override
	public String toString(@Nullable Event e, boolean debug) {
		return getExpr().toString(e, debug) + " with lore " + lore.toString(e, debug);
	}
}
