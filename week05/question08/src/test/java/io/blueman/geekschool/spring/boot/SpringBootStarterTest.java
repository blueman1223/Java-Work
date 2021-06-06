/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.blueman.geekschool.spring.boot;

import io.blueman.geekschool.spring.boot.core.School;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.Extensions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.core.env.Environment;
import org.springframework.test.context.TestPropertySource;

import javax.annotation.Resource;

@SpringBootApplication
@SpringBootTest(classes = SpringBootStarterTest.class)
@ExtendWith(value = {OutputCaptureExtension.class})
public class SpringBootStarterTest {
    
    @Resource(name = "geekSchool")
    private School geekSchool;

    @Test
    public void assertDing(CapturedOutput output) {
        geekSchool.ding();
        assert output.getAll().contains("students");
        System.out.println(geekSchool);
        assert output.getOut().contains("hello-geek-school");
    }

}
