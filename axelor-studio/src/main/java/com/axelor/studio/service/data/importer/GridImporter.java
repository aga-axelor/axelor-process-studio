/**
 * Axelor Business Solutions
 *
 * Copyright (C) 2018 Axelor (<http://axelor.com>).
 *
 * This program is free software: you can redistribute it and/or  modify
 * it under the terms of the GNU Affero General Public License, version 3,
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.axelor.studio.service.data.importer;

import java.util.List;

import com.axelor.exception.AxelorException;
import com.axelor.meta.db.MetaField;
import com.axelor.meta.db.MetaModel;
import com.axelor.meta.db.MetaModule;
import com.axelor.studio.db.ViewBuilder;
import com.axelor.studio.db.ViewItem;
import com.axelor.studio.db.repo.ViewBuilderRepository;
import com.axelor.studio.service.ViewLoaderService;
import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;

public class GridImporter {
	
	@Inject
	private ViewBuilderRepository viewBuilderRepo;
	
	@Inject
	private ViewLoaderService viewLoaderService;
	
	@Transactional
	public ViewBuilder createGridView(MetaModule module, MetaModel model, List<MetaField> fields) throws AxelorException {
		
		if (fields == null || fields.isEmpty()) {
			return viewLoaderService.getDefaultGrid(module.getName(), model, true);
		}
		
		String viewName = ViewLoaderService.getDefaultViewName(model.getName(),
				"grid");

		ViewBuilder viewBuilder = viewLoaderService.getViewBuilder(module.getName(), viewName, "grid");
		if (viewBuilder == null) {
			viewBuilder = new ViewBuilder(viewName);
			viewBuilder.setMetaModule(module);
		}

		viewBuilder.setViewType("grid");
		viewBuilder.setMetaModel(model);
		viewBuilder.setModel(model.getFullName());
		viewBuilder.setEdited(true);
		String title = model.getTitle();
		if(Strings.isNullOrEmpty(title)) {
			title = model.getName();
		}
		viewBuilder.setTitle(title);
		viewBuilder.clearViewItemList();
		
		int seq = 0;
		for (MetaField field : fields) {
			ViewItem viewItem = new ViewItem(field.getName());
			viewItem.setFieldType(field.getFieldType());
			viewItem.setMetaField(field);
			viewItem.setSequence(seq);
			viewBuilder.addViewItemListItem(viewItem);
			
			seq++;
		}

		return viewBuilderRepo.save(viewBuilder);

	}
	
	@Transactional
	public void clearGrid(String module, String model) {
    	
		String gridName = ViewLoaderService.getDefaultViewName(model, "grid");
		
		ViewBuilder grid = viewLoaderService.getViewBuilder(module, gridName, "grid");
		
		if (grid != null) {
			viewBuilderRepo.remove(grid);
		}
		
	}

	

}
