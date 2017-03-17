/*
 * Copyright (c) 2016 BreizhCamp
 * [http://breizhcamp.org]
 *
 * This file is part of CFP.io.
 *
 * CFP.io is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package io.cfp.controller;

import io.cfp.domain.exception.NotFoundException;
import io.cfp.dto.RateAdmin;
import io.cfp.entity.Role;
import io.cfp.service.RateAdminService;
import io.cfp.service.admin.user.AdminUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = {"/v0/rates", "/api/rates" }, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class AdminRateController {

    @Autowired
    private AdminUserService adminUserService;

    @Autowired
    private RateAdminService rateService;

    /**
     * Get all ratings
     */
    @RequestMapping(method = RequestMethod.GET)
    @Secured(Role.ADMIN)
    public List<RateAdmin> getRates() {
        return rateService.getAll();
    }

    /**
     * Delete all ratings
     */
    @RequestMapping(method = RequestMethod.DELETE)
    @Secured(Role.ADMIN)
    public void deleteRates() {
        rateService.deleteAll();
    }

    /**
     * Add a new rating
     */
    @RequestMapping(method=RequestMethod.POST)
    @Secured(Role.REVIEWER)
    public RateAdmin postRate(@Valid @RequestBody RateAdmin rate) throws NotFoundException {
        return rateService.add(rate, adminUserService.getCurrentUser(), rate.getTalkId());
    }

    /**
     * Edit a rating
     */
    @RequestMapping(value= "/{rateId}", method=RequestMethod.PUT)
    @Secured(Role.REVIEWER)
    public RateAdmin putRate(@PathVariable int rateId, @Valid @RequestBody RateAdmin rate) {
        rate.setId(rateId);
        return rateService.edit(rate);
    }

    /**
     * Get a specific rating
     */
    @RequestMapping(value= "/{rateId}", method= RequestMethod.GET)
    @Secured(Role.REVIEWER)
    // FIXME only return owned rate for reviewer
    public RateAdmin getRate(@PathVariable int rateId) {
        return rateService.get(rateId);
    }

    /**
     * Get all rating for a given user
     * @param userId
     * @return
     */
    @RequestMapping(value= "/user/{userId}", method= RequestMethod.GET)
    @Secured(Role.ADMIN)
    public List<RateAdmin> getRateByUserId(@PathVariable int userId) {
        return rateService.findForUser(userId);
    }

    /**
     * Get all ratings for a given session
     */
    @RequestMapping(value= "/proposals/{talkId}", method= RequestMethod.GET)
    @Secured(Role.ADMIN)
    public List<RateAdmin> getRatesByTalkId(@PathVariable int talkId) {
        return rateService.findForTalk(talkId);
    }

    /**
     * Get rating for current user and a session
     */
    @RequestMapping(value= "/proposals/{talkId}/me", method = RequestMethod.GET)
    @Secured(Role.REVIEWER)
    public RateAdmin getRateByRowIdAndUserId(@PathVariable int talkId) throws NotFoundException {
        int adminId = adminUserService.getCurrentUser().getId();
        return rateService.findForTalkAndAdmin(talkId, adminId);
    }

    /**
     * Delete specific rating
     */
    @RequestMapping(value= "/{rateId}", method= RequestMethod.DELETE)
    @Secured(Role.REVIEWER)
    // FIXME only return owned rate for reviewer
    public void deleteRate(@PathVariable int rateId) {
        rateService.delete(rateId);
    }


    /**
     * Get Rates stats
     */
    @RequestMapping(value = "/stats", method = RequestMethod.GET)
    @Secured(Role.ADMIN)
    public Map<String, Long> getRateStats() {
        return rateService.getRateByEmailUsers();
    }
}
