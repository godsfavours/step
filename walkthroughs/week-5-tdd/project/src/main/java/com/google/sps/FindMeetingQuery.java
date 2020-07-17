// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;


public final class FindMeetingQuery {
    private ArrayList<TimeRange> availableTimes = new ArrayList<>();

    public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
        availableTimes.clear();

        // If the request is too long, return empty ArrayList
        if (request.getDuration() > TimeRange.WHOLE_DAY.duration()) {
            return availableTimes;
        }

        /* Any time is available during the start. Time ranges will 
            be dynamically added and removed from the availableTimes 
            array as the program loops through the events */
        availableTimes.add(TimeRange.WHOLE_DAY);
        
        // If the set of attendees is empty, the entire day is available
        if (request.getAttendees().isEmpty() && request.getOptionalAttendees().isEmpty()) {
            return availableTimes;
        }

        Collection<String> attendees = request.getAttendees();
        Collection<String> optionalAttendees = request.getOptionalAttendees();

        // Boolean: mandatoryAttendee
        Map<String, Boolean> attendeesMap = new HashMap<String, Boolean>();
        for (String attendee : attendees) { 
            attendeesMap.put(attendee, true); 
        }
        for (String attendee : optionalAttendees) { 
            attendeesMap.put(attendee, false); 
        }

        System.out.println(attendeesMap.toString());

        /* Events stored in Map to allow to dynamically remove events in loop (so as not to 
           work on the same event that is shared by multiple attendees) */
        Map<Event, Boolean> eventsMap = new HashMap<Event, Boolean>();
        for (Event event : events) { 
            eventsMap.put(event, true); 
        }

        Iterator eventsIterator; 
        for (Map.Entry<String, Boolean> attendeeEntry : attendeesMap.entrySet()) {
            String attendee = attendeeEntry.getKey();

            eventsIterator = eventsMap.entrySet().iterator();
            while(eventsIterator.hasNext()) {

                Map.Entry pair = (Map.Entry) eventsIterator.next();
                if ((Boolean) pair.getValue()) {
                    Event event = (Event) pair.getKey();

                    Collection<String> eventAttendees = event.getAttendees();
                    if (eventAttendees.contains(attendee)) {
                        TimeRange timeRange = event.getWhen();
                        blockTimeRange(timeRange, request.getDuration(), attendeeEntry.getValue());

                        if (attendeeEntry.getValue()) {
                            eventsIterator.remove();
                        }      
                        // If there are only optional attendees & no times are available for them
                        if (!attendeeEntry.getValue() && availableTimes.isEmpty()) {
                            availableTimes.add(TimeRange.WHOLE_DAY);
                        }
                    }
                } 
            }
        } 
        return availableTimes;
    }

    public void blockTimeRange(TimeRange blockedTimeRange, long meetingDuration, boolean mandatory) {
        for (int i = 0; i < availableTimes.size(); i++) {
            TimeRange timeRange = availableTimes.get(i);

            if ((blockedTimeRange.equals(TimeRange.WHOLE_DAY) && !mandatory) 
                || blockedTimeRange.duration() < meetingDuration) return;

            if (timeRange.overlaps(blockedTimeRange)) {

                TimeRange newTimeRange1 = TimeRange.fromStartEnd(timeRange.start(), 
                                                blockedTimeRange.start(), false);
                TimeRange newTimeRange2 = TimeRange.fromStartEnd(blockedTimeRange.end(), 
                                                timeRange.end(), false);      
                /*
                            |--blocked--|
                        |-----timeRange----|
                */
                if (timeRange.contains(blockedTimeRange)) {
                    System.out.println(timeRange.toString() + " contains " + blockedTimeRange.toString());
                    if (blockedTimeRange.start() != timeRange.start()) {
                        createNewTimeRange(i, meetingDuration, newTimeRange1);
                    }
                    if (blockedTimeRange.end() != timeRange.end()) {
                        createNewTimeRange(i + 1, meetingDuration, newTimeRange2);
                    }        
                }
                /*
                            |--blocked--|
                    |--timeRange--|
                */
                else if (blockedTimeRange.end() > timeRange.end()) {
                    System.out.println(blockedTimeRange.toString() + " overlaps end of " + timeRange.toString());
                    createNewTimeRange(i, meetingDuration, newTimeRange1);    
                }
                /*
                    |--blocked--|
                          |--timeRange--|
                */
                else if (blockedTimeRange.start() < timeRange.start()) {
                    System.out.println(blockedTimeRange.toString() + " overlaps start of " + timeRange.toString());
                    createNewTimeRange(i, meetingDuration, newTimeRange2);              
                }
                availableTimes.remove(timeRange);
                return;
            }
        }
    }

    public void createNewTimeRange(int index, long meetingDuration, TimeRange timeRange) {
        if (timeRange.duration() >= meetingDuration) {
            availableTimes.add(index, timeRange);
        }
    }
}
