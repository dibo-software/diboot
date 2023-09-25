/*
 * Copyright (c) 2015-2020, www.dibo.ltd (service@dibo.ltd).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package diboot.core.test.binder.service.impl;

import com.diboot.core.service.impl.BaseServiceImpl;
import diboot.core.test.binder.entity.ProductRel;
import diboot.core.test.binder.mapper.ProductRelMapper;
import diboot.core.test.binder.service.ProductRelService;
import org.springframework.stereotype.Service;

/**
 * 产品关联相关Service
 * @author mazc@dibo.ltd
 * @version 2023/09/14
 */
@Service
public class ProductRelServiceImpl extends BaseServiceImpl<ProductRelMapper, ProductRel> implements ProductRelService {

}
