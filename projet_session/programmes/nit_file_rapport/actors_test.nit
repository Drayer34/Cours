		# This file is generated by nitactors (threaded version)
		# Do not modify, instead use the generated services.
		module actors_test is
	no_warning("missing-doc")
end

import test

####################### Redef classes #########################
redef class A
redef var async is lazy do return new ProxyA.proxy(self)
end



####################### Actor classes #########################
class ActorA
	super Actor
	redef type E: nullable A
end



####################### Messages classes ######################
class MessageA
	super Message
	redef type E: A
end



class AMessagefoo
	super MessageA

	

	redef fun invoke(instance) do instance.foo
end



class AMessagetestfoo
	super MessageA

	

	redef fun invoke(instance) do instance.testfoo
end



####################### Proxy classes #########################
redef class ProxyA

	redef type E: ActorA
	#var actor: ActorA is noinit

	init proxy(base_class: A) do
		actor = new ActorA(base_class)
		actor.start
	end

	redef fun foo do
	var msg = new AMessagefoo
	actor.mailbox.push(msg)
	
end


redef fun testfoo do
	var msg = new AMessagetestfoo
	actor.mailbox.push(msg)
	
end

end



