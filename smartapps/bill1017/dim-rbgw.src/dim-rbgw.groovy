/**
 *  Dim RBGW 
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
    name: "Dim RBGW ",
    namespace: "Bill1017",
    author: "Bill Hess",
    description: "Dimmer switch (button with 2 button)",
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
	subscribe(button, "button", dimmerPressed)
    log.debug "Subscribe Button"
    subscribe (master, "level", levelChanged)

}

def levelChanged (evt)
{
	def dimLevel = master.currentValue("level")
    def hueLevel = master.currentValue("hue")
	def saturationLevel = master.currentValue("saturation")

	def newValue = [hue: hueLevel, saturation: saturationLevel, level: dimLevel as Integer]
    log.debug "Level Changed New value is: ${newValue}"    
}

def changeLevel (delta)
{
	def currentState = button.currentState("button")
    def buttonState = currentState.data
    def index = buttonState.indexOf(":") + 1
    def index2 = buttonState.indexOf("}")
    def buttonNumber = buttonState.substring(index,index2)
    log.trace "button $number"
    
    def switchState = master.currentValue ("switch")
    log.debug "Switch State $switchState"
    // for some reason the level state is never 0, it is the last non-0 value
    // setting level to 0 just turn off the device
    // the follow in required to set level at the correct new value if previous setLevel was to 0
	def dimLevel = switchState == "on" ? master.currentValue("level") : 0
 
    switch (buttonNumber) {
    	case "1":
 			dimLevel = dimLevel + delta > 100 ? 100 : dimLevel + delta
            break;
        case "2":
        	dimLevel = dimLevel - delta < 0 ? 0 : dimLevel - delta
            break;
        default :
        	log.debug "Unknow Button Number $buttonNumber"
            
    }
    master.setLevel(dimLevel)
}


def dimmerPressed(evt)
{
	def currentState = button.currentState("button")
	log.trace "dimmerPressed($evt.name: $evt.value)"
    log.debug "Button State $currentState.value"
    switch (currentState.value){
    	case "pushed":
          	changeLevel (10)
            break
         case "held":
         	  	changeLevel (25)
                break
        default :
        	log.trace "Unknown state currentState.value $currentState.value"
 	}
    
    def temperature = master.currentValue("colorTemperature")
    log.debug "Color Temperture = $temperature"
    
    // setting the color temperature turns off the the RGB LED and turns on the W LED
    // (setting the hue, saturation, or colorMap will do the opposite) 
    master.setColorTemperature(temperature)
    


}
