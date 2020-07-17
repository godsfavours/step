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
import java.util.HashSet;



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
        if (request.getAttendees().isEmpty()) {
            return availableTimes;
        }

        // Loop through the attendees in MeetingRequest to find events that they're in
        Collection<String> attendees = request.getAttendees();
        Set<Event> eventsSet = new HashSet<Event>(events);
        for (String attendee : attendees) {
            System.out.println("Attendee: " + attendees);
            for (Event event : eventsSet) {
                Collection<String> eventAttendees = event.getAttendees();
                if (eventAttendees.contains(attendee)) {
                    System.out.println("Event attendees: " + eventAttendees.toString());
                    //eventsSet.remove(event);
                    TimeRange timeRange = event.getWhen();
                    System.out.println("Event when: " + timeRange.toString());
                    blockTimeRange(timeRange, request.getDuration());
                    System.out.println("New available times: " + availableTimes.toString());
                  
                }
                
            }

        }
        

        
        return availableTimes;
    }

    // Binary search function to find the TimeRange object to split
    public void blockTimeRange(TimeRange blockedTimeRange, long meetingDuration) {
        System.out.println("Blocking time... ");
        for (int i = 0; i < availableTimes.size(); i++) {
            TimeRange timeRange = availableTimes.get(i);
            if (timeRange.overlaps(blockedTimeRange)) {
                if (blockedTimeRange.contains(timeRange)) {
                    availableTimes.remove(timeRange);
                    return;
                }
                /*
                             |--blocked--|
                          |-----timeRange----|
                */
                else if (timeRange.contains(blockedTimeRange)) {
                    System.out.println(timeRange.toString() + " contains " + blockedTimeRange.toString());
                    if (blockedTimeRange.start() != timeRange.start()) {
                        TimeRange newTimeRange1 = TimeRange.fromStartEnd(timeRange.start(), 
                                                blockedTimeRange.start(), false);
                        if (newTimeRange1.duration() >= meetingDuration) {
                            availableTimes.add(i, newTimeRange1);
                        }
                    }
                    if (blockedTimeRange.end() != timeRange.end()) {
                         TimeRange newTimeRange2 = TimeRange.fromStartEnd(blockedTimeRange.end(), 
                                                timeRange.end(), false);
                        if (newTimeRange2.duration() >= meetingDuration) {
                            availableTimes.add(i + 1, newTimeRange2);
                        }
                    }    
                    availableTimes.remove(timeRange);
                    return;

                }
                /*
                            |--blocked--|
                    |--timeRange--|
                */
                else if (blockedTimeRange.end() > timeRange.end()) {
                    System.out.println(blockedTimeRange.toString() + " overlaps end of " + timeRange.toString());
                    TimeRange newTimeRange = TimeRange.fromStartEnd(timeRange.start(), 
                                                blockedTimeRange.start(), false);
                    if (newTimeRange.duration() >= meetingDuration) {
                        availableTimes.add(i, newTimeRange);
                    }
                    availableTimes.remove(timeRange);
                    return;
                }
                /*
                    |--blocked--|
                          |--timeRange--|
                */
                else if (blockedTimeRange.start() < timeRange.start()) {
                    System.out.println(blockedTimeRange.toString() + " overlaps front of " + timeRange.toString());
                    TimeRange newTimeRange = TimeRange.fromStartEnd(blockedTimeRange.end(), 
                                                timeRange.end(), false);
                    if (newTimeRange.duration() >= meetingDuration) {
                        availableTimes.add(i, newTimeRange);
                    }
                    availableTimes.remove(timeRange);
                    return;
                }
            }
        }
    }
}




