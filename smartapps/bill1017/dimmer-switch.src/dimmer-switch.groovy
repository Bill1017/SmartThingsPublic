/**
 *  Dimmer Switch
 *
 *  Copyright 2017 Bill Hess
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */
definition(
    name: "Dimmer Switch",
    namespace: "Bill1017",
    author: "Bill Hess",
    description: "Dim Lights",
    category: "Convenience",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {

	section("Dimmer") {
		input "button", "capability.button", multiple: false, required: true, title: "Dimmer Buttons"
	}

	section("Master Light") {
    	input "master", "capability.colorControl", title: "Colored Light"
    }
}

def installed() {
	log.debug "Installed with settings: ${settings}"

	initialize()
}

def updated() {
	log.debug "Updated with settings: ${settings}"

	unsubscribe()
	initialize()

}


def initialize() {
	subscribe(button, "button.pushed", dimmerPressed)
    log.debug "Subscribe Button"

}
def dimmerPressed(evt)
{
	def currentState = button.currentState("button")
	log.trace "dimmerPressed($evt.name: $evt.value)"
    def buttonNumber = currentState.data
    def index = buttonNumber.indexOf(":") + 1
    def index2 = buttonNumber.indexOf("}")
    def number = buttonNumber.substring(index,index2)
    log.trace "button $number"
    
/*    def temperature = master.currentValue("colorTemperature") 
    def dimLevel = master.currentValue("level")
    
   log.debug "temp: $temperature level: $dimLevel"
*/   
   	
    def dimLevel = master.currentValue("level")
	if (number == "1") {
 		if (dimLevel+10>100) {
    		dimLevel = 100
    	} else {
    		dimLevel = dimLevel + 10
    	}
    }
	else {
 		if (dimLevel-10 <0) {
    		dimLevel = 0
    	} else {
    		dimLevel = dimLevel - 10
    	}
    }
    master.setLevel(dimLevel)

    def hueLevel = master.currentValue("hue")

    def saturationLevel = master.currentValue("saturation")
    

	def newValue = [hue: hueLevel, saturation: saturationLevel, level: dimLevel as Integer]
    log.debug "New value is: ${newValue}"
    def temperature = master.currentValue("colorTemperature")
    master.setColorTemperature(temperature)
    
    
  
    
 }
// TODO: implement event handlers